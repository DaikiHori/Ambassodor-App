package com.cambassador.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CodeDao {

    @Query("SELECT * from codes ORDER BY id ASC")
    fun getAllCodes(): Flow<List<Code>>

    @Query("SELECT * from codes WHERE id = :id")
    fun getCodes(id: Int): Flow<Code>

    @Query("SELECT * from codes WHERE eventId = :eventId AND usable = 1 AND used = 0 ORDER BY id ASC LIMIT 1")
    fun getFirstByEventIdStream(eventId: Int): Flow<Code>

    @Query("SELECT * from codes WHERE eventId = :eventId")
    fun getAllCodesByEventId(eventId: Int): Flow<List<Code>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(code: Code)

    @Update
    suspend fun update(code: Code)

    @Delete
    suspend fun delete(code: Code)
}