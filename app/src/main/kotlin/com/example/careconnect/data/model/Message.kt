package com.example.careconnect.data.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Message(
    @DocumentId
    var id: String? = null,
    val senderId: String = "", // Crucial for knowing who sent the message
    val senderName: String = "",
    val text: String = "",
    val timestamp: Date = Date()
    // 'isFromCurrentUser' has been removed as it's a UI concern, not a data one.
)

