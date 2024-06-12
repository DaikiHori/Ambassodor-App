package com.example.inventory.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.Event
import com.example.inventory.data.EventAndCodes
import com.example.inventory.data.EventsRepository
import com.example.inventory.ui.event.EventEditDestination
import com.example.inventory.ui.event.EventUiState
import com.example.inventory.ui.event.toEvent
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val eventsRepository: EventsRepository,
) : ViewModel() {
    val homeUiState: StateFlow<HomeUiState> =
        eventsRepository.getAllEventsStream().map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * Ui State for HomeScreen
 */
data class HomeUiState(val eventList: List<EventAndCodes> = listOf())
