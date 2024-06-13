package com.ambassador.app.data

import kotlinx.coroutines.flow.Flow

interface CodesRepository {
    fun getAllCodesStream(): Flow<List<Code>>

    fun getCodeStream(id: Int): Flow<Code?>

    fun getFirstByEventIdStream(eventId: Int): Flow<Code?>

    suspend fun insertCode(codes: Code)

    suspend fun deleteCode(code: Code)


    suspend fun updateCode(code: Code)
}