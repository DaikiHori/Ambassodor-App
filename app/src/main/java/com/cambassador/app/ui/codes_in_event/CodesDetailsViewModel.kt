package com.cambassador.app.ui.codes_in_event

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cambassador.app.data.Code
import com.cambassador.app.data.CodesRepository
import com.cambassador.app.data.EventsRepository
import com.cambassador.app.ui.event.EventDetailsUiState
import com.cambassador.app.ui.event.EventDetailsViewModel
import com.cambassador.app.ui.event.EventDetailsViewModel.Companion
import com.cambassador.app.ui.event.EventUiState
import com.cambassador.app.ui.event.toEventDetails
import com.cambassador.app.ui.event.toEventUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CodesDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val eventsRepository: EventsRepository,
    private val codesRepository: CodesRepository
) : ViewModel() {
    private val eventId: Int = checkNotNull(savedStateHandle[CodesDetailsDestination.eventIdArg])
    val eventUiState: StateFlow<EventDetailsUiState> =
        eventsRepository.getEventStream(eventId)
            .filterNotNull()
            .map {
                EventDetailsUiState(outOfStock = it.event.name != "", eventDetails = it.event.toEventDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = EventDetailsUiState()
            )


    val codesUiState: StateFlow<CodesDetailsUiState> =
        codesRepository.getAllCodesByEventIdStream(eventId)
            .filterNotNull()
            .map {
                CodesDetailsUiState(it)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CodesDetailsUiState()
            )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class CodesDetailsUiState(
    val codeList: List<Code> = listOf()
)
