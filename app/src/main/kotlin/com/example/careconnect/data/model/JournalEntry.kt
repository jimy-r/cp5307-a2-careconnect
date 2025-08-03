package com.example.careconnect.data.model

import java.util.Date

data class JournalEntry(
    val id: String,
    val authorName: String,
    val note: String,
    val timestamp: Date
)

