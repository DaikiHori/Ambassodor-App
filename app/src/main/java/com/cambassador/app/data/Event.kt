package com.cambassador.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "events")
@TypeConverters(DateConverters::class)
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val date: Date,
    val url: String,
    val count: Int = 0,
    val usable_count: Int= 0
)
