package com.cambassador.app.ui.codes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cambassador.app.data.Code
import com.cambassador.app.data.EventsRepository
import com.cambassador.app.data.CodesRepository
import com.cambassador.app.data.User
import com.cambassador.app.data.UsersRepository
import com.cambassador.app.ui.event.EventDetails
import com.cambassador.app.ui.event.EventDetailsDestination
import com.cambassador.app.ui.event.EventUiState
import com.cambassador.app.ui.event.toEventUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import android.util.Log
import com.cambassador.app.data.Event
import kotlinx.coroutines.flow.map

class CodesViewModel(
    savedStateHandle: SavedStateHandle,
    private val eventsRepository: EventsRepository,
    private val codesRepository: CodesRepository,
    private val usersRepository: UsersRepository
) : ViewModel() {
    var codesUiState by mutableStateOf(CodesUiState())
        private set
    var eventUiState by mutableStateOf(EventUiState())
    private val eventId: Int = checkNotNull(savedStateHandle[EventDetailsDestination.eventIdArg])

    private var userUiState = MutableStateFlow<List<User>?>(null)
    val users = userUiState

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

    fun searchUsers(name: String) {
        viewModelScope.launch {
            usersRepository.getAllUsersByNameStream(name)
                .collect{ data -> userUiState.value = data }
        }
    }
}

data class CodesUiState(
    val codesDetails: CodesDetails = CodesDetails(id = 0,code = "", eventId = 0, used = false, usable = true, userName = ""),
    val codes: Code? =  Code(id = 0, eventId = 0,code = "", usable = true, used = false, userName = "")
)

data class UsersUiState(
    val usersDetails: UsersDetails = UsersDetails(id = 0, name = "")
)

data class CodesDetails(
    val id: Int = 0,
    val eventId: Int = 0,
    val code: String= "",
    val used: Boolean = false,
    val usable: Boolean = true,
    val userName: String = ""
)

data class EventDetails(
    val id: Int = 0,
    val name: String = "",
    val date: Date = Date(),
    val url: String = ""
)

data class UsersDetails(
    val id: Int = 0,
    val name: String = ""
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

fun User.toUsersDetails(): UsersDetails = UsersDetails(
    id = id,
    name = name
)

fun UsersDetails.toUser(): User = User(
    id = id,
    name = name
)