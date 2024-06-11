package com.example.inventory.data
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.type.DateTime
import java.util.Date
import com.example.inventory.data.DateConverters

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
