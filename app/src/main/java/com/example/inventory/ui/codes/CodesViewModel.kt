package com.example.inventory.ui.codes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.Code
import com.example.inventory.data.EventsRepository
import com.example.inventory.data.CodesRepository
import com.example.inventory.data.EventAndCodes
import com.example.inventory.ui.event.EventDetails
import com.example.inventory.ui.event.EventDetailsDestination
import com.example.inventory.ui.event.EventUiState
import com.example.inventory.ui.event.toEventUiState
import com.example.inventory.ui.home.HomeUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CodesViewModel(
    savedStateHandle: SavedStateHandle,
    private val eventsRepository: EventsRepository,
    private val codesRepository: CodesRepository
) : ViewModel() {
    var codesUiState by mutableStateOf(CodesUiState())
        private set

    private val eventId: Int = checkNotNull(savedStateHandle[EventDetailsDestination.eventIdArg])

    init {
        viewModelScope.launch {
            codesUiState = codesRepository.getFirstByEventIdStream(eventId)
                .filterNotNull()
                .first()
                .toCodesUiState()
        }
    }
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun updateUiState(codeDetails: CodesDetails) {
        codesUiState =
            CodesUiState(codesDetails = codeDetails)
    }

    suspend fun updateCode(){
        codesRepository.updateCode(codesUiState.codesDetails.toCode())
    }
}

data class CodesUiState(
    val codesDetails: CodesDetails = CodesDetails(id = 0,code = "", eventId = 0, used = false, usable = true, userName = ""),
    val codes: Code? =  Code(id = 0, eventId = 0,code = "", usable = true, used = false, userName = "")
)

data class Code(
    val id: Int,
    val eventId : Int,
    val code: String,
    val used: Boolean,
    val usable: Boolean,
    val userName: String
)

data class CodesDetails(
    val id: Int = 0,
    val eventId: Int = 0,
    val code: String= "",
    val used: Boolean = false,
    val usable: Boolean = true,
    val userName: String = ""
)

fun Code.toCodesUiState() :CodesUiState = CodesUiState(
    codesDetails = this.toCodesDetails()
)

fun Code.toCodesDetails() :CodesDetails = CodesDetails(
    id = id,
    eventId = eventId,
    code = code,
    used = used,
    usable = usable,
    userName = userName
)

fun CodesDetails.toCode(): Code = Code(
    id = id,
    eventId = eventId,
    code = code,
    used = used,
    usable = usable,
    userName = userName
)