package com.example.inventory.ui.codes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.EventsRepository
import com.example.inventory.data.CodesRepository
import com.example.inventory.data.EventAndCodes
import com.example.inventory.ui.event.EventDetailsDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CodesViewModel(
    savedStateHandle: SavedStateHandle,
    private val eventsRepository: EventsRepository,
    private val codesRepository: CodesRepository
) : ViewModel() {
        private val eventId: Int = checkNotNull(savedStateHandle[EventDetailsDestination.eventIdArg])
        val CodeUiState: StateFlow<CodesUiState> =
            codesRepository.getCodeStream(eventId)
                .filterNotNull()
                .map {
                    CodesUiState(outOfStock = it.code != "")
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(CodesViewModel.TIMEOUT_MILLIS),
                    initialValue = CodesUiState()
                )


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class CodesUiState(
    val codes: Codes =  Codes(id = 0,code = "", usable = true, used = false, user = ""),
    val outOfStock: Boolean = true
)

data class Codes(
    val id: Int,
    val code: String,
    val used: Boolean,
    val usable: Boolean,
    val user: String
)

fun CodesUiState.toEventAndCodes(): EventAndCodes = EventAndCodes(

)