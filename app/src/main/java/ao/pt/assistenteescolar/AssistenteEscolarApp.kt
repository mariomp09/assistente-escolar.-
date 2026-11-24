package ao.pt.assistenteescolar

import android.app.Application
import ao.pt.assistenteescolar.data.local.AppDatabase
import ao.pt.assistenteescolar.data.local.UserPreferencesRepository
import ao.pt.assistenteescolar.data.network.NetworkMonitor
import ao.pt.assistenteescolar.data.remote.GeminiPrepClient
import ao.pt.assistenteescolar.data.repository.SchoolRepository
import ao.pt.assistenteescolar.widget.ScheduleWidgetWorker

class AssistenteEscolarApp : Application() {

    lateinit var database: AppDatabase
        private set
    lateinit var preferences: UserPreferencesRepository
        private set
    lateinit var networkMonitor: NetworkMonitor
        private set
    lateinit var repository: SchoolRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
        preferences = UserPreferencesRepository(this)
        networkMonitor = NetworkMonitor(this)
        repository = SchoolRepository(
            context = this,
            scheduleDao = database.scheduleDao(),
            noteDao = database.noteDao(),
            prepPackDao = database.prepPackDao(),
            geminiClient = GeminiPrepClient()
        )
        ScheduleWidgetWorker.enqueuePeriodic(this)
    }
}
