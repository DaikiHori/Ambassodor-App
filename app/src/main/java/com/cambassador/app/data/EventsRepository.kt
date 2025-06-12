package com.cambassador.app.data

import kotlinx.coroutines.flow.Flow

interface EventsRepository {

    fun getAllEventsStream(): Flow<List<EventAndCodes>>

    fun getAllEventsWithCodeCountStream(): Flow<List<Event>>

    fun getEventStream(id: Int): Flow<EventAndCodes?>

    suspend fun insertEvent(event: Event): Long

    suspend fun deleteEvent(event: Event)

    suspend fun updateEvent(event: Event)
}