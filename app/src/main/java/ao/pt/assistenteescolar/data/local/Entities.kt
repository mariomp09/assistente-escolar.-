package ao.pt.assistenteescolar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

enum class CountryCurriculum(val label: String, val flagCode: String) {
    ANGOLA("Angola (INIDE)", "AO"),
    PORTUGAL("Portugal (DGE)", "PT")
}

enum class SchoolShift(val label: String) {
    MORNING("Manhã"),
    AFTERNOON("Tarde")
}

@Entity(tableName = "schedule_classes")
data class ScheduleClassEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayOfWeek: Int, // 1 = Segunda ... 6 = Sábado
    val subject: String,
    val startTime: String, // "07:30"
    val endTime: String,   // "09:00"
    val room: String,
    val teacher: String
)

@Entity(tableName = "study_notes")
data class StudyNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val title: String,
    val content: String,
    val updatedAtMillis: Long = System.currentTimeMillis()
)

@Serializable
data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

@Entity(tableName = "prep_packs")
data class PrepPackEntity(
    @PrimaryKey val id: String,
    val countryCode: String, // "AO" ou "PT"
    val grade: String,
    val subject: String,
    val topic: String,
    val title: String,
    val overview: String,
    val keyPointsJson: String,
    val tipsJson: String,
    val questionsJson: String,
    val source: String, // "starter" ou "gemini"
    val createdAtMillis: Long = System.currentTimeMillis(),
    val lastScore: Int? = null
)
