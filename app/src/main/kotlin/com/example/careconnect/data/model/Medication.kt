package com.example.careconnect.data.model

import java.util.Date

data class Medication(
    val id: String,
    val name: String,
    val dosage: String,
    val time: Date,
    var isTaken: Boolean = false,
    val administeredBy: String? = null
)

