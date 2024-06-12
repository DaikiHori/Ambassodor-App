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

    suspend fun updateEvent() {
        if (validateInput(eventUiState.eventDetails)) {
            eventsRepository.updateEvent(eventUiState.eventDetails.toEvent())
        }
    }

    suspend fun deleteEvent(){
        eventsRepository.deleteEvent(eventUiState.eventDetails.toEvent())
    }

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
