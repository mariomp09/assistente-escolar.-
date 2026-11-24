package ao.pt.assistenteescolar.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedule_classes ORDER BY dayOfWeek ASC, startTime ASC")
    fun observeAll(): Flow<List<ScheduleClassEntity>>

    @Query("SELECT * FROM schedule_classes WHERE dayOfWeek = :day ORDER BY startTime ASC")
    suspend fun getForDaySync(day: Int): List<ScheduleClassEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ScheduleClassEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ScheduleClassEntity>)

    @Update
    suspend fun update(item: ScheduleClassEntity)

    @Delete
    suspend fun delete(item: ScheduleClassEntity)

    @Query("DELETE FROM schedule_classes")
    suspend fun clearAll()
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM study_notes ORDER BY updatedAtMillis DESC")
    fun observeAll(): Flow<List<StudyNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: StudyNoteEntity): Long

    @Delete
    suspend fun delete(note: StudyNoteEntity)
}

@Dao
interface PrepPackDao {
    @Query("SELECT * FROM prep_packs WHERE countryCode = :countryCode ORDER BY createdAtMillis DESC")
    fun observeByCountry(countryCode: String): Flow<List<PrepPackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(packs: List<PrepPackEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(pack: PrepPackEntity)

    @Query("UPDATE prep_packs SET lastScore = :score WHERE id = :id")
    suspend fun updateScore(id: String, score: Int)
}
