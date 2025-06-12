package com.cambassador.app.data

import kotlinx.coroutines.flow.Flow

class OfflineUsersRepository(private val userDao: UserDao): UsersRepository{

    override fun getAllUsersStream(): Flow<List<User>> = userDao.getAllUsers()

    override fun getUsersStream(id: Int): Flow<User?> = userDao.getUsers(id)

    override fun getAllUsersByNameStream(name: String): Flow<List<User>> = userDao.getAllUsersByName(name)

    override suspend fun insertUser(user: User): Long = userDao.insert(user)

    override suspend fun updateUser(user: User) = userDao.update(user)

    override suspend fun deleteUser(user: User) = userDao.delete(user)
}