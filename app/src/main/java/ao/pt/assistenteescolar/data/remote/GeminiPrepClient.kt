package ao.pt.assistenteescolar.data.remote

import ao.pt.assistenteescolar.BuildConfig
import ao.pt.assistenteescolar.data.local.CountryCurriculum
import ao.pt.assistenteescolar.data.local.PrepPackEntity
import ao.pt.assistenteescolar.data.local.QuizQuestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

@Serializable
private data class GeneratedPrepPayload(
    val title: String,
    val overview: String,
    val keyPoints: List<String>,
    val formulasOrTips: List<String>,
    val questions: List<QuizQuestion>
)

class GeminiPrepClient {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun generatePrepPack(
        country: CountryCurriculum,
        grade: String,
        subject: String,
        topic: String
    ): Result<PrepPackEntity> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank()) {
            return@withContext Result.failure(
                IllegalStateException("Adicione GEMINI_API_KEY em local.properties para gerar online.")
            )
        }

        val curriculumRule = if (country == CountryCurriculum.ANGOLA) {
            "Currículo oficial de Angola (INIDE), nível $grade. Use terminologia escolar angolana."
        } else {
            "Currículo oficial de Portugal (DGE / Aprendizagens Essenciais), nível $grade. Use português europeu."
        }

        val prompt = """
            $curriculumRule
            Disciplina: $subject
            Tema da prova: $topic

            Responda APENAS em JSON válido com as chaves exatas:
            {
              "title": "Título curto",
              "overview": "Síntese em 2 frases diretas ao ponto",
              "keyPoints": ["4 pontos-chave objetivos"],
              "formulasOrTips": ["2 fórmulas ou dicas de prova"],
              "questions": [
                {
                  "question": "Enunciado claro",
                  "options": ["Opção A", "Opção B", "Opção C", "Opção D"],
                  "correctIndex": 0,
                  "explanation": "Explicação curta da resposta correta"
                }
              ]
            }
            Gere exatamente 4 questões de escolha múltipla.
        """.trimIndent()

        runCatching {
            val models = listOf("gemini-2.5-flash-lite", "gemini-2.5-flash")
            var lastError: Throwable? = null
            var candidateText: String? = null

            for (model in models) {
                try {
                    val endpoint = URL(
                        "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
                    )
                    val conn = (endpoint.openConnection() as HttpURLConnection).apply {
                        requestMethod = "POST"
                        connectTimeout = 10_000
                        readTimeout = 20_000
                        doOutput = true
                        setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                    }

                    val body = """
                        {
                          "contents": [{"parts": [{"text": ${json.encodeToString(prompt)}}]}],
                          "generationConfig": {
                            "responseMimeType": "application/json",
                            "temperature": 0.3
                          }
                        }
                    """.trimIndent()

                    OutputStreamWriter(conn.outputStream, Charsets.UTF_8).use { it.write(body) }

                    val status = conn.responseCode
                    val stream = if (status in 200..299) conn.inputStream else conn.errorStream
                    val rawResponse = stream.bufferedReader().use { it.readText() }
                    if (status == 503 || status == 429) {
                        lastError = IllegalStateException("Serviço temporariamente ocupado ($status).")
                        continue
                    }
                    if (status !in 200..299) {
                        throw IllegalStateException("Erro HTTP $status da API Gemini.")
                    }

                    val root = json.parseToJsonElement(rawResponse).jsonObject
                    candidateText = root["candidates"]?.jsonArray?.firstOrNull()
                        ?.jsonObject?.get("content")?.jsonObject
                        ?.get("parts")?.jsonArray?.firstOrNull()
                        ?.jsonObject?.get("text")?.jsonPrimitive?.content
                    if (!candidateText.isNullOrBlank()) break
                } catch (err: Throwable) {
                    lastError = err
                }
            }

            val finalJson = candidateText ?: throw (lastError ?: IllegalStateException("Resposta sem texto estruturado."))
            val payload = json.decodeFromString<GeneratedPrepPayload>(finalJson)
            PrepPackEntity(
                id = "gemini-${UUID.randomUUID()}",
                countryCode = country.flagCode,
                grade = grade,
                subject = subject,
                topic = topic,
                title = payload.title,
                overview = payload.overview,
                keyPointsJson = json.encodeToString(payload.keyPoints),
                tipsJson = json.encodeToString(payload.formulasOrTips),
                questionsJson = json.encodeToString(payload.questions),
                source = "gemini"
            )
        }
    }
}
