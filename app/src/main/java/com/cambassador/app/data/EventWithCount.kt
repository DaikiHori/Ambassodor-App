package com.cambassador.app.data

data class EventWithCount(
    val event: Event,
    val codes: List<Code>,
    val count: Int,
    val usable_count: Int,
)
