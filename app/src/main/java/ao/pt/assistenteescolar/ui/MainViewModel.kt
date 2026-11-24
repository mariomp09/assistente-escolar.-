package ao.pt.assistenteescolar.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ao.pt.assistenteescolar.AssistenteEscolarApp
import ao.pt.assistenteescolar.data.local.CountryCurriculum
import ao.pt.assistenteescolar.data.local.PrepPackEntity
import ao.pt.assistenteescolar.data.local.ScheduleClassEntity
import ao.pt.assistenteescolar.data.local.SchoolShift
import ao.pt.assistenteescolar.data.local.StudyNoteEntity
import ao.pt.assistenteescolar.data.local.UserSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as AssistenteEscolarApp
    private val prefs = app.preferences
    private val repo = app.repository

    val settings: StateFlow<UserSettings> = prefs.settingsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        UserSettings()
    )

    val isOnline: StateFlow<Boolean> = app.networkMonitor.isOnlineFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        app.networkMonitor.isCurrentlyOnline()
    )

    val schedule: StateFlow<List<ScheduleClassEntity>> = repo.scheduleFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val notes: StateFlow<List<StudyNoteEntity>> = repo.notesFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val prepPacks: StateFlow<List<PrepPackEntity>> = settings
        .flatMapLatest { s -> repo.prepPacksFlow(s.country) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating

    private val _generationError = MutableStateFlow<String?>(null)
    val generationError: StateFlow<String?> = _generationError

    fun saveCurriculumSetup(
        studentName: String,
        country: CountryCurriculum,
        grade: String,
        shift: SchoolShift,
        isDarkTheme: Boolean,
        replaceDefaultSchedule: Boolean
    ) {
        viewModelScope.launch {
            prefs.saveSettings(studentName, country, grade, shift, isDarkTheme, onboardingDone = true)
            if (replaceDefaultSchedule) {
                repo.resetScheduleForCountry(country, shift)
            }
        }
    }

    fun toggleDarkTheme() {
        viewModelScope.launch {
            prefs.setDarkTheme(!settings.value.isDarkTheme)
        }
    }

    fun saveClass(item: ScheduleClassEntity) {
        viewModelScope.launch { repo.addOrUpdateClass(item) }
    }

    fun deleteClass(item: ScheduleClassEntity) {
        viewModelScope.launch { repo.deleteClass(item) }
    }

    fun addNote(subject: String, title: String, content: String) {
        viewModelScope.launch { repo.addNote(subject, title, content) }
    }

    fun deleteNote(note: StudyNoteEntity) {
        viewModelScope.launch { repo.deleteNote(note) }
    }

    fun generatePrep(subject: String, topic: String, onSuccess: (PrepPackEntity) -> Unit) {
        val current = settings.value
        if (!isOnline.value) {
            _generationError.value = "Sem ligação à internet. Consulte ou refaça os simulados já guardados."
            return
        }
        viewModelScope.launch {
            _isGenerating.value = true
            _generationError.value = null
            val res = repo.generateAndSavePrepPack(current.country, current.grade, subject, topic)
            _isGenerating.value = false
            res.onSuccess(onSuccess).onFailure { err ->
                _generationError.value = err.message ?: "Erro ao contactar o serviço Gemini."
            }
        }
    }

    fun recordQuizScore(packId: String, scorePercent: Int) {
        viewModelScope.launch { repo.saveQuizScore(packId, scorePercent) }
    }

    fun clearError() {
        _generationError.value = null
    }
}
