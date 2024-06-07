/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.inventory.ui.event

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.EventsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

/**
 * ViewModel to retrieve, update and delete an event from the [EventsRepository]'s data source.
 */
class EventDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val eventsRepository: EventsRepository,
) : ViewModel() {

    private val eventId: Int = checkNotNull(savedStateHandle[EventDetailsDestination.eventIdArg])

    /**
     * Holds the event details ui state. The data is retrieved from [EventsRepository] and mapped to
     * the UI state.
     */
    val uiState: StateFlow<EventDetailsUiState> =
        eventsRepository.getEventStream(eventId)
            .filterNotNull()
            .map {
                EventDetailsUiState(outOfStock = it.name != "", eventDetails = it.toEventDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = EventDetailsUiState()
            )


    /**
     * Deletes the event from the [EventsRepository]'s data source.
     */
    suspend fun deleteEvent() {
        eventsRepository.deleteEvent(uiState.value.eventDetails.toEvent())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * UI state for EventDetailsScreen
 */
data class EventDetailsUiState(
    val outOfStock: Boolean = true,
    val eventDetails: EventDetails = EventDetails(id = 0, name = "name", date = Date())
)
