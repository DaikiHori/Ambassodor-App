package com.cambassador.app.data

import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    fun getAllUsersStream(): Flow<List<User>>

    fun getUsersStream(id: Int): Flow<User?>

    fun getAllUsersByNameStream(name: String): Flow<List<User>>

    suspend fun insertUser(user: User): Long

    suspend fun updateUser(user: User)

    suspend fun deleteUser(user: User)
}