package com.cambassador.app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "codes",
    foreignKeys = [ForeignKey(
        entity = Event::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("eventId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Code(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val number: Int = 0,
    @ColumnInfo(name = "eventId", index = true)
    val eventId: Int,
    val code: String,
    val usable: Boolean = true,
    val used: Boolean = false,
    val userName: String = ""
)
