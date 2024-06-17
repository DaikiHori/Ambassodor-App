package com.ambassador.app.ui.codes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ambassador.app.data.Code
import com.ambassador.app.data.EventsRepository
import com.ambassador.app.data.CodesRepository
import com.ambassador.app.ui.event.EventDetails
import com.ambassador.app.ui.event.EventDetailsDestination
import com.ambassador.app.ui.event.EventUiState
import com.ambassador.app.ui.event.toEventUiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date

class CodesViewModel(
    savedStateHandle: SavedStateHandle,
    private val eventsRepository: EventsRepository,
    private val codesRepository: CodesRepository
) : ViewModel() {
    var codesUiState by mutableStateOf(CodesUiState())
        private set
    var eventUiState by mutableStateOf(EventUiState())
    private val eventId: Int = checkNotNull(savedStateHandle[EventDetailsDestination.eventIdArg])

    init {
        viewModelScope.launch {
            codesUiState = codesRepository.getFirstByEventIdStream(eventId)
                .filterNotNull()
                .first()
                .toCodesUiState()
            eventUiState = eventsRepository.getEventStream(eventId)
                .filterNotNull()
                .first().event.toEventUiState()
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

data class Event(
    val id: Int = 0,
    val name: String = "",
    val date: Date = Date(),
    val url: String = ""
)
data class EventDetails(
    val id: Int = 0,
    val name: String = "",
    val date: Date = Date(),
    val url: String = ""
)
fun Event.toEventUiState() :EventUiState = EventUiState(
    eventDetails = this.toEventDetails()
)
fun Event.toEventDetails() :EventDetails = EventDetails(
    id = id,
    name = name,
    date = date,
    url = url
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