package com.cambassador.app.data

import kotlinx.coroutines.flow.Flow

class OfflineCodesRepository(private val codeDao: CodeDao) : CodesRepository {

    override fun getAllCodesStream(): Flow<List<Code>> = codeDao.getAllCodes()

    override fun getCodesStream(id: Int): Flow<Code?> = codeDao.getCodes(id)

    override fun getAllCodesByEventIdStream(id: Int): Flow<List<Code>> = codeDao.getAllCodesByEventId(id)

    override fun getFirstByEventIdStream(eventId: Int): Flow<Code?> = codeDao.getFirstByEventIdStream(eventId)

    override suspend fun insertCode(code: Code) = codeDao.insert(code)

    override suspend fun deleteCode(code: Code) = codeDao.delete(code)

    override suspend fun updateCode(code: Code) = codeDao.update(code)
}