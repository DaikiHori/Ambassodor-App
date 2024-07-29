package com.cambassador.app.ui.codes_in_event

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cambassador.app.data.Code
import com.cambassador.app.data.CodesRepository
import com.cambassador.app.data.EventsRepository
import com.cambassador.app.data.UsersRepository
import com.cambassador.app.ui.event.EventDetailsUiState
import com.cambassador.app.ui.event.toEventDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CodesDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val eventsRepository: EventsRepository,
    private val codesRepository: CodesRepository,
    private val usersRepository: UsersRepository
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

    private var codesState = MutableStateFlow<List<Code>?>(null)
    val displayCodes: StateFlow<List<Code>?> = codesState

    init{
        viewModelScope.launch {
            codesRepository.getAllCodesByEventIdStream(eventId)
                .collect{ data -> codesState.value = data }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun updateCode(id: Int,data: Code){
        val update = codesState.value?.map { if (it.id == id) data else it }
        codesState.value = update
    }

    fun saveCode(codesId :Int) {
        val data = codesState.value?.find{it.id == codesId}
        if (data != null){
            viewModelScope.launch {
                codesRepository.updateCode(data)
            }
        }
    }
}