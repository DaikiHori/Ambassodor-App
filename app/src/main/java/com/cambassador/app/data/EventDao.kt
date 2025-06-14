package com.cambassador.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Transaction
    @Query("SELECT * from events ORDER BY id ASC")
    fun getAllEvents(): Flow<List<EventAndCodes>>

    @Transaction
    @Query("SELECT e.*, COUNT(c.id) AS count,COUNT(CASE WHEN usable = 1 AND used = 0 THEN c.id END) as usable_count " +
            "FROM events e " +
            "LEFT JOIN codes c ON e.id = c.eventId " +
            "GROUP BY e.id")
    fun getAllEventsWithCodeCount(): Flow<List<Event>>

    @Transaction
    @Query("SELECT * from events WHERE id = :id")
    fun getEvent(id: Int): Flow<EventAndCodes>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: Event): Long

    @Update
    suspend fun update(event: Event)

    @Delete
    suspend fun delete(event: Event)
}