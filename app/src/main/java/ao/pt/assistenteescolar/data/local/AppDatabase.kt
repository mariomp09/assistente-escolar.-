package ao.pt.assistenteescolar.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ScheduleClassEntity::class, StudyNoteEntity::class, PrepPackEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
    abstract fun noteDao(): NoteDao
    abstract fun prepPackDao(): PrepPackDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "assistente_escolar.db"
            )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val instance = getInstance(context)
                            instance.scheduleDao().insertAll(StarterPackData.defaultAngolaSchedule())
                            instance.noteDao().insert(
                                StudyNoteEntity(
                                    subject = "Matemática",
                                    title = "Fórmula Resolvente",
                                    content = "Calcular sempre o discriminante Δ = b² - 4ac antes de achar as raízes."
                                )
                            )
                            instance.prepPackDao().insertAll(StarterPackData.embeddedPrepPacks())
                        }
                    }
                })
                .build()
        }
    }
}
