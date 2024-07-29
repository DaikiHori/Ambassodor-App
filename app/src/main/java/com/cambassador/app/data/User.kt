package com.cambassador.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    //indices = [Index(value = ["name"], unique = true)]
)
class User (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)
