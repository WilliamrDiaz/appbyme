package com.byme.app.domain.model

data class Review(
    val id: String = "",
    val professionalId: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Double = 0.0,
    val comment: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)