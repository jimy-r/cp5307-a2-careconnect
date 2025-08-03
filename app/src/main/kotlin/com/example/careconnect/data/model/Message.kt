package com.example.careconnect.data.model

import java.util.Date

data class Message(
    val id: String,
    val senderName: String,
    val text: String,
    val timestamp: Date,
    val isFromCurrentUser: Boolean
)

