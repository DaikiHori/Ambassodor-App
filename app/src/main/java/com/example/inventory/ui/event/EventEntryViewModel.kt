/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.inventory.ui.event

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.inventory.data.Code
import com.example.inventory.data.CodesRepository
import com.example.inventory.data.Event
import com.example.inventory.data.EventAndCodes
import com.example.inventory.data.EventsRepository
import java.text.NumberFormat
import java.util.Date

/**
 * ViewModel to validate and insert events in the Room database.
 */
class EventEntryViewModel(
    private val eventsRepository: EventsRepository,
    private val codesRepository: CodesRepository
) : ViewModel() {

    /**
     * Holds current event ui state
     */
    var eventUiState by mutableStateOf(EventUiState())
        private set

    /**
     * Updates the [eventUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(eventDetails: EventDetails) {
        eventUiState =
            EventUiState(eventDetails = eventDetails, isEntryValid = validateInput(eventDetails))
    }

    /**
     * Inserts an [Event] in the Room database
     */
    suspend fun saveEvent() {
        if (validateInput()) {
            eventsRepository.insertEvent(eventUiState.eventDetails.toEvent())
            val codes = eventUiState.eventDetails.code.split(",")
            val id = eventUiState.eventDetails.id
            for(c in codes){
                val code = Code(eventId = id,code = c)
                codesRepository.insertCode(code)
            }
        }
    }

    private fun validateInput(uiState: EventDetails = eventUiState.eventDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && date.toString().isNotBlank()
        }
    }
}

/**
 * Represents Ui State for an Event.
 */
data class EventUiState(
    val eventDetails: EventDetails = EventDetails(id = 1,name= "name",date = Date(),code = ""),
    val isEntryValid: Boolean = false
)

data class EventDetails(
    val id: Int = 0,
    val name: String = "",
    val date: Date,
    val code: String = ""
)

/**
 * Extension function to convert [EventUiState] to [Event]. If the value of [EventDetails.price] is
 * not a valid [Double], then the price will be set to 0.0. Similarly if the value of
 * [EventUiState] is not a valid [Int], then the quantity will be set to 0
 */
fun EventDetails.toEvent(): Event = Event(
    id = id,
    name = name,
    date = date
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

/**
 * Extension function to convert [Event] to [EventUiState]
 */
fun Event.toEventUiState(isEntryValid: Boolean = false): EventUiState = EventUiState(
    eventDetails = this.toEventDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Event] to [EventDetails]
 */
fun Event.toEventDetails(): EventDetails = EventDetails(
    id = id,
    name = name,
    date = date
)
