package com.byme.app.domain.model

data class Message(
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
)