package com.example.inventory.data

import kotlinx.coroutines.flow.Flow

interface CodesRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllCodesStream(): Flow<List<Code>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getCodeStream(id: Int): Flow<Code?>

    /**
     * Insert item in the data source
     */
    suspend fun insertCode(code: Code)

    /**
     * Delete item from the data source
     */
    suspend fun deleteCode(code: Code)

    /**
     * Update item in the data source
     */
    suspend fun updateCode(code: Code)
}