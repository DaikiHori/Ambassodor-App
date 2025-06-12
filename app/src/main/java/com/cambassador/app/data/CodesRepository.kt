package com.cambassador.app.data

import kotlinx.coroutines.flow.Flow

interface CodesRepository {
    fun getAllCodesStream(): Flow<List<Code>>

    fun getCodesStream(id: Int): Flow<Code?>

    fun getFirstByEventIdStream(eventId: Int): Flow<Code?>

    fun getAllCodesByEventIdStream(eventId: Int): Flow<List<Code>>

    suspend fun insertCode(codes: Code)

    suspend fun deleteCode(code: Code)

    suspend fun updateCode(code: Code)
}