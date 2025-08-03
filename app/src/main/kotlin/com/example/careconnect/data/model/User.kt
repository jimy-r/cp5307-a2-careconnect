package com.example.careconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val careCircleId: String = "",
    val phone: String = "",
    val address: String = ""
)

