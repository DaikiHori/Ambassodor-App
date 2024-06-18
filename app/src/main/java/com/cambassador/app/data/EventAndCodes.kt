package com.cambassador.app.data

import androidx.room.Embedded
import androidx.room.Relation

class EventAndCodes {
    @Embedded
    lateinit var event: Event
    @Relation(parentColumn = "id", entityColumn = "eventId")
    lateinit var codes: List<Code>
}