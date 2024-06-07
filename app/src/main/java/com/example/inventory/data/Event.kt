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
@Entity(tableName = "events")
@TypeConverters(DateConverters::class)
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val date: Date
)
