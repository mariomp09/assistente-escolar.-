package ao.pt.assistenteescolar.data.local

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object StarterPackData {
    private val json = Json { ignoreUnknownKeys = true }

    fun defaultAngolaSchedule(shift: SchoolShift = SchoolShift.MORNING): List<ScheduleClassEntity> =
        if (shift == SchoolShift.AFTERNOON) {
            listOf(
                ScheduleClassEntity(dayOfWeek = 1, subject = "Matemática", startTime = "13:00", endTime = "14:30", room = "Sala 12", teacher = "Prof. Mateus"),
                ScheduleClassEntity(dayOfWeek = 1, subject = "Língua Portuguesa", startTime = "14:45", endTime = "16:15", room = "Sala 12", teacher = "Prof.ª Carla"),
                ScheduleClassEntity(dayOfWeek = 1, subject = "Física", startTime = "16:30", endTime = "18:00", room = "Lab. 02", teacher = "Prof. António"),
                ScheduleClassEntity(dayOfWeek = 2, subject = "Química", startTime = "13:00", endTime = "14:30", room = "Lab. 01", teacher = "Prof.ª Helena"),
                ScheduleClassEntity(dayOfWeek = 2, subject = "Biologia", startTime = "14:45", endTime = "16:15", room = "Sala 14", teacher = "Prof. Domingos"),
                ScheduleClassEntity(dayOfWeek = 3, subject = "Matemática", startTime = "13:00", endTime = "14:30", room = "Sala 12", teacher = "Prof. Mateus"),
                ScheduleClassEntity(dayOfWeek = 4, subject = "Física", startTime = "13:00", endTime = "14:30", room = "Lab. 02", teacher = "Prof. António"),
                ScheduleClassEntity(dayOfWeek = 5, subject = "Língua Portuguesa", startTime = "13:00", endTime = "14:30", room = "Sala 12", teacher = "Prof.ª Carla")
            )
        } else {
            listOf(
                ScheduleClassEntity(dayOfWeek = 1, subject = "Matemática", startTime = "07:30", endTime = "09:00", room = "Sala 12", teacher = "Prof. Mateus"),
                ScheduleClassEntity(dayOfWeek = 1, subject = "Língua Portuguesa", startTime = "09:15", endTime = "10:45", room = "Sala 12", teacher = "Prof.ª Carla"),
                ScheduleClassEntity(dayOfWeek = 1, subject = "Física", startTime = "11:00", endTime = "12:30", room = "Lab. 02", teacher = "Prof. António"),
                ScheduleClassEntity(dayOfWeek = 2, subject = "Química", startTime = "07:30", endTime = "09:00", room = "Lab. 01", teacher = "Prof.ª Helena"),
                ScheduleClassEntity(dayOfWeek = 2, subject = "Biologia", startTime = "09:15", endTime = "10:45", room = "Sala 14", teacher = "Prof. Domingos"),
                ScheduleClassEntity(dayOfWeek = 3, subject = "Matemática", startTime = "07:30", endTime = "09:00", room = "Sala 12", teacher = "Prof. Mateus"),
                ScheduleClassEntity(dayOfWeek = 4, subject = "Física", startTime = "07:30", endTime = "09:00", room = "Lab. 02", teacher = "Prof. António"),
                ScheduleClassEntity(dayOfWeek = 5, subject = "Língua Portuguesa", startTime = "07:30", endTime = "09:00", room = "Sala 12", teacher = "Prof.ª Carla")
            )
        }

    fun defaultPortugalSchedule(shift: SchoolShift = SchoolShift.MORNING): List<ScheduleClassEntity> =
        if (shift == SchoolShift.AFTERNOON) {
            listOf(
                ScheduleClassEntity(dayOfWeek = 1, subject = "Matemática A", startTime = "14:00", endTime = "15:30", room = "Bloco B · 204", teacher = "Prof. Ricardo"),
                ScheduleClassEntity(dayOfWeek = 1, subject = "Português", startTime = "15:45", endTime = "17:15", room = "Bloco A · 108", teacher = "Prof.ª Inês"),
                ScheduleClassEntity(dayOfWeek = 1, subject = "Física e Química A", startTime = "17:30", endTime = "19:00", room = "Lab. FQ2", teacher = "Prof. Tiago"),
                ScheduleClassEntity(dayOfWeek = 2, subject = "Biologia e Geologia", startTime = "14:00", endTime = "15:30", room = "Lab. BG1", teacher = "Prof.ª Sofia"),
                ScheduleClassEntity(dayOfWeek = 3, subject = "Matemática A", startTime = "14:00", endTime = "15:30", room = "Bloco B · 204", teacher = "Prof. Ricardo"),
                ScheduleClassEntity(dayOfWeek = 4, subject = "Física e Química A", startTime = "14:00", endTime = "15:30", room = "Lab. FQ2", teacher = "Prof. Tiago"),
                ScheduleClassEntity(dayOfWeek = 5, subject = "Português", startTime = "14:30", endTime = "16:00", room = "Bloco A · 108", teacher = "Prof.ª Inês")
            )
        } else {
            listOf(
                ScheduleClassEntity(dayOfWeek = 1, subject = "Matemática A", startTime = "08:30", endTime = "10:00", room = "Bloco B · 204", teacher = "Prof. Ricardo"),
                ScheduleClassEntity(dayOfWeek = 1, subject = "Português", startTime = "10:15", endTime = "11:45", room = "Bloco A · 108", teacher = "Prof.ª Inês"),
                ScheduleClassEntity(dayOfWeek = 1, subject = "Física e Química A", startTime = "12:00", endTime = "13:30", room = "Lab. FQ2", teacher = "Prof. Tiago"),
                ScheduleClassEntity(dayOfWeek = 2, subject = "Biologia e Geologia", startTime = "08:30", endTime = "10:00", room = "Lab. BG1", teacher = "Prof.ª Sofia"),
                ScheduleClassEntity(dayOfWeek = 3, subject = "Matemática A", startTime = "08:30", endTime = "10:00", room = "Bloco B · 204", teacher = "Prof. Ricardo"),
                ScheduleClassEntity(dayOfWeek = 4, subject = "Física e Química A", startTime = "08:30", endTime = "10:00", room = "Lab. FQ2", teacher = "Prof. Tiago"),
                ScheduleClassEntity(dayOfWeek = 5, subject = "Português", startTime = "09:00", endTime = "10:30", room = "Bloco A · 108", teacher = "Prof.ª Inês")
            )
        }

    fun embeddedPrepPacks(): List<PrepPackEntity> {
        val aoMathQuestions = listOf(
            QuizQuestion(
                question = "Para 2x² - 8x + 6 = 0, qual o valor de Δ e o número de raízes reais?",
                options = listOf("Δ = 16; duas raízes reais distintas", "Δ = 0; uma raiz dupla", "Δ = -16; sem raízes reais", "Δ = 64; duas raízes iguais"),
                correctIndex = 0,
                explanation = "Δ = (-8)² - 4·2·6 = 64 - 48 = 16 > 0, logo há duas raízes reais distintas (x = 1 e x = 3)."
            ),
            QuizQuestion(
                question = "Qual é a soma e o produto das raízes de x² - 7x + 10 = 0?",
                options = listOf("S = -7 e P = 10", "S = 7 e P = 10", "S = 7 e P = -10", "S = 10 e P = 7"),
                correctIndex = 1,
                explanation = "Relações de Viète: S = -b/a = 7 e P = c/a = 10."
            ),
            QuizQuestion(
                question = "Na função f(x) = -x² + 4x - 3, a parábola tem:",
                options = listOf("Concavidade para cima e mínimo em x = 2", "Concavidade para baixo e máximo em x = 2", "Concavidade para baixo e máximo em x = -2", "Concavidade para cima e máximo em x = 4"),
                correctIndex = 1,
                explanation = "Como a = -1 < 0, a concavidade é voltada para baixo e Xv = -4 / (2·(-1)) = 2."
            )
        )

        val ptMathQuestions = listOf(
            QuizQuestion(
                question = "Qual é o domínio de f(x) = √(x - 3) / (x - 5)?",
                options = listOf("[3, +∞[", "[3, +∞[ \\ {5}", "]3, 5[", "ℝ \\ {5}"),
                correctIndex = 1,
                explanation = "x - 3 ≥ 0 ⇒ x ≥ 3, e o denominador exige x ≠ 5. Logo Df = [3, +∞[ \\ {5}."
            ),
            QuizQuestion(
                question = "Se f é função ímpar e f(4) = -7, quanto vale f(-4)?",
                options = listOf("-7", "0", "7", "1/7"),
                correctIndex = 2,
                explanation = "Numa função ímpar, f(-x) = -f(x). Portanto f(-4) = -(-7) = 7."
            ),
            QuizQuestion(
                question = "O gráfico de g(x) = (x + 2)² - 1 resulta de y = x² por translação de vetor:",
                options = listOf("(2, -1)", "(-2, -1)", "(-2, 1)", "(2, 1)"),
                correctIndex = 1,
                explanation = "f(x + 2) - 1 desloca 2 unidades para a esquerda (-2) e 1 unidade para baixo (-1)."
            )
        )

        return listOf(
            PrepPackEntity(
                id = "starter-ao-mat",
                countryCode = "AO",
                grade = "10.ª Classe",
                subject = "Matemática",
                topic = "Equações e Funções Quadráticas",
                title = "Funções e Equações do 2.º Grau",
                overview = "Resumo focado no currículo angolano (INIDE): fórmula resolvente, discriminante e estudo do vértice.",
                keyPointsJson = json.encodeToString(listOf(
                    "Forma canónica: ax² + bx + c = 0 (a ≠ 0).",
                    "Discriminante Δ = b² - 4ac define o número de raízes reais.",
                    "Vértice da parábola: Xv = -b/(2a) e Yv = -Δ/(4a)."
                )),
                tipsJson = json.encodeToString(listOf(
                    "Fórmula Resolvente: x = (-b ± √Δ) / (2a)",
                    "Relações de Viète: Soma = -b/a e Produto = c/a."
                )),
                questionsJson = json.encodeToString(aoMathQuestions),
                source = "starter"
            ),
            PrepPackEntity(
                id = "starter-pt-mat",
                countryCode = "PT",
                grade = "10.º Ano",
                subject = "Matemática A",
                topic = "Funções Reais e Paridade",
                title = "Domínio, Paridade e Transformações",
                overview = "Síntese alinhada com as Aprendizagens Essenciais em Portugal para fichas de avaliação e Exame Nacional.",
                keyPointsJson = json.encodeToString(listOf(
                    "Domínio: excluir zeros no denominador e radicandos negativos em raízes de índice par.",
                    "Função par: f(-x) = f(x); Função ímpar: f(-x) = -f(x).",
                    "Translação g(x) = f(x - h) + k segundo o vetor u = (h, k)."
                )),
                tipsJson = json.encodeToString(listOf(
                    "Escreve intervalos com notação oficial portuguesa: [3, +∞[ \\ {5}.",
                    "Em f(x + h), o gráfico desloca-se h unidades para a esquerda."
                )),
                questionsJson = json.encodeToString(ptMathQuestions),
                source = "starter"
            )
        )
    }
}
