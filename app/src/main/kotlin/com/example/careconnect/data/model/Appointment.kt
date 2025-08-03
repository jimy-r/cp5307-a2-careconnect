package com.example.careconnect.data.model

import java.util.Date

data class Appointment(
    val id: String,
    val title: String,
    val specialist: String,
    val location: String,
    val dateTime: Date
)

