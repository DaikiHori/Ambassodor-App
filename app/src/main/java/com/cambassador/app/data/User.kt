package com.cambassador.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
class User (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)
