package com.ambassador.app.ui.event

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ambassador.app.data.Code
import com.ambassador.app.data.CodesRepository
import com.ambassador.app.data.Event
import com.ambassador.app.data.EventAndCodes
import com.ambassador.app.data.EventsRepository
import java.text.NumberFormat
import java.util.Date

class EventEntryViewModel(
    private val eventsRepository: EventsRepository,
    private val codesRepository: CodesRepository
) : ViewModel() {

    var eventUiState by mutableStateOf(EventUiState())
        private set

    fun updateUiState(eventDetails: EventDetails) {
        eventUiState =
            EventUiState(eventDetails = eventDetails, isEntryValid = validateInput(eventDetails))
    }

    suspend fun saveEvent() {
        if (validateInput()) {
            try {
                val eventId = eventsRepository.insertEvent(eventUiState.eventDetails.toEvent())
                val codes = eventUiState.eventDetails.code
                    .replace("\"","")
                    .replace("\r",",").replace("\n",",")
                    .split(",")
                val id = eventId.toInt()
                for (c in codes) {
                    if(c.isNotEmpty()) {
                        val code = Code(eventId = id, code = c)
                        codesRepository.insertCode(code)
                    }
                }
            }catch (e: Throwable){
                throw e
            }
        }
    }

    private fun validateInput(uiState: EventDetails = eventUiState.eventDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && date.toString().isNotBlank()
        }
    }

}

data class EventUiState(
    val eventDetails: EventDetails = EventDetails(id = 0,name= "",date = Date(),code = "",url = ""),
    val isEntryValid: Boolean = false
)

data class EventDetails(
    val id: Int = 0,
    val name: String = "",
    val date: Date,
    val code: String = "",
    val url: String = ""
)

fun EventDetails.toEvent(): Event = Event(
    id = id,
    name = name,
    date = date,
    url = url
)

fun EventDetails.toCodes(): Code = Code(
    eventId = id,
    code = code
)

fun EventDetails.toEventAndCodes(): EventAndCodes = EventAndCodes(

)
fun Event.formatedDate(): String {
    return NumberFormat.getCurrencyInstance().format(date)
}

fun Event.toEventUiState(isEntryValid: Boolean = false): EventUiState = EventUiState(
    eventDetails = this.toEventDetails(),
    isEntryValid = isEntryValid
)

fun Event.toEventDetails(): EventDetails = EventDetails(
    id = id,
    name = name,
    date = date,
    url = url
)