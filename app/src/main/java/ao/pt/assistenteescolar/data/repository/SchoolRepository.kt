package ao.pt.assistenteescolar.data.repository

import android.content.Context
import ao.pt.assistenteescolar.data.local.CountryCurriculum
import ao.pt.assistenteescolar.data.local.NoteDao
import ao.pt.assistenteescolar.data.local.PrepPackDao
import ao.pt.assistenteescolar.data.local.PrepPackEntity
import ao.pt.assistenteescolar.data.local.ScheduleClassEntity
import ao.pt.assistenteescolar.data.local.ScheduleDao
import ao.pt.assistenteescolar.data.local.SchoolShift
import ao.pt.assistenteescolar.data.local.StarterPackData
import ao.pt.assistenteescolar.data.local.StudyNoteEntity
import ao.pt.assistenteescolar.data.remote.GeminiPrepClient
import ao.pt.assistenteescolar.widget.ScheduleWidgetUpdater
import kotlinx.coroutines.flow.Flow

class SchoolRepository(
    private val context: Context,
    private val scheduleDao: ScheduleDao,
    private val noteDao: NoteDao,
    private val prepPackDao: PrepPackDao,
    private val geminiClient: GeminiPrepClient
) {
    val scheduleFlow: Flow<List<ScheduleClassEntity>> = scheduleDao.observeAll()
    val notesFlow: Flow<List<StudyNoteEntity>> = noteDao.observeAll()

    fun prepPacksFlow(country: CountryCurriculum): Flow<List<PrepPackEntity>> =
        prepPackDao.observeByCountry(country.flagCode)

    suspend fun addOrUpdateClass(item: ScheduleClassEntity) {
        if (item.id == 0L) scheduleDao.insert(item) else scheduleDao.update(item)
        ScheduleWidgetUpdater.updateAll(context)
    }

    suspend fun deleteClass(item: ScheduleClassEntity) {
        scheduleDao.delete(item)
        ScheduleWidgetUpdater.updateAll(context)
    }

    suspend fun resetScheduleForCountry(country: CountryCurriculum, shift: SchoolShift) {
        scheduleDao.clearAll()
        val defaults = if (country == CountryCurriculum.ANGOLA) {
            StarterPackData.defaultAngolaSchedule(shift)
        } else {
            StarterPackData.defaultPortugalSchedule(shift)
        }
        scheduleDao.insertAll(defaults)
        ScheduleWidgetUpdater.updateAll(context)
    }

    suspend fun addNote(subject: String, title: String, content: String) {
        noteDao.insert(StudyNoteEntity(subject = subject, title = title, content = content))
    }

    suspend fun deleteNote(note: StudyNoteEntity) {
        noteDao.delete(note)
    }

    suspend fun generateAndSavePrepPack(
        country: CountryCurriculum,
        grade: String,
        subject: String,
        topic: String
    ): Result<PrepPackEntity> {
        val result = geminiClient.generatePrepPack(country, grade, subject, topic)
        result.onSuccess { pack -> prepPackDao.upsert(pack) }
        return result
    }

    suspend fun saveQuizScore(packId: String, scorePercent: Int) {
        prepPackDao.updateScore(packId, scorePercent)
    }
}
