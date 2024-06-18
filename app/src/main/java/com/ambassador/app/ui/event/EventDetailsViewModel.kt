package com.ambassador.app.ui.event

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ambassador.app.data.EventsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Date

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
                EventDetailsUiState(outOfStock = it.event.name != "", eventDetails = it.event.toEventDetails())
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
    val eventDetails: EventDetails = EventDetails(id = 0, name = "", date = Date(),code = "",url = "")
)
