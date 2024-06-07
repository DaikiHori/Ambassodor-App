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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.EventsRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve and update an event from the [EventsRepository]'s data source.
 */
class EventEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val eventsRepository: EventsRepository
) : ViewModel() {

    /**
     * Holds current event ui state
     */
    var eventUiState by mutableStateOf(EventUiState())
        private set

    private val eventId: Int = checkNotNull(savedStateHandle[EventEditDestination.eventIdArg])

    init {
        viewModelScope.launch {
            eventUiState = eventsRepository.getEventStream(eventId)
                .filterNotNull()
                .first()
                .event.toEventUiState(true)
        }
    }

    /**
     * Update the event in the [EventsRepository]'s data source
     */
    suspend fun updateEvent() {
        if (validateInput(eventUiState.eventDetails)) {
            eventsRepository.updateEvent(eventUiState.eventDetails.toEvent())
        }
    }

    /**
     * Updates the [eventUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(eventDetails: EventDetails) {
        eventUiState =
            EventUiState(eventDetails = eventDetails, isEntryValid = validateInput(eventDetails))
    }

    private fun validateInput(uiState: EventDetails = eventUiState.eventDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && date.toString().isNotBlank()
        }
    }
}
