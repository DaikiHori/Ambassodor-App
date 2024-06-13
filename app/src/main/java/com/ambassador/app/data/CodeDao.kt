package com.ambassador.app.data

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
    fun getCode(id: Int): Flow<Code>

    @Query("SELECT * from codes WHERE eventId = :eventId AND usable = TRUE AND used = FALSE ORDER BY id ASC LIMIT 1")
    fun getFirstByEventIdStream(eventId: Int): Flow<Code>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(code: Code)

    @Update
    suspend fun update(code: Code)

    @Delete
    suspend fun delete(code: Code)
}