package com.cambassador.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * from users ORDER BY id ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * from users WHERE id = :id")
    fun getUsers(id: Int): Flow<User>

    @Query("SELECT * from users WHERE name LIKE :name || '%'")
    fun getAllUsersByName(name: String): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)
}