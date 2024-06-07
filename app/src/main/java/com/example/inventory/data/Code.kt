package com.example.inventory.data
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.type.DateTime
import java.util.Date
import com.example.inventory.data.DateConverters

/**
 * Entity data class represents a single row in the database.
 */
@Entity(tableName = "codes")
@TypeConverters(DateConverters::class)
data class Code(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val eventId: Int,
    val code: String
)
