package com.ambassador.app.data

import kotlinx.coroutines.flow.Flow
interface EventsRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllEventsStream(): Flow<List<EventAndCodes>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getEventStream(id: Int): Flow<EventAndCodes?>

    /**
     * Insert item in the data source
     */
    suspend fun insertEvent(event: Event): Long

    /**
     * Delete item from the data source
     */
    suspend fun deleteEvent(event: Event)

    /**
     * Update item in the data source
     */
    suspend fun updateEvent(event: Event)
}