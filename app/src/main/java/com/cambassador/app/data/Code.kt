package com.cambassador.app.data
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Entity data class represents a single row in the database.
 */
@Entity(tableName = "codes",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = Event::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("eventId"),
            onDelete = ForeignKey.CASCADE
        )
    )
)
data class Code(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val eventId: Int,
    val code: String,
    val usable: Boolean = true,
    val used: Boolean = false,
    val userName: String = ""
)
