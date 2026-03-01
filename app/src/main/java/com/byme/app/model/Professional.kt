package com.byme.app.model

data class Professional(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val description: String = "",
    val phone: String = "",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val imageUrl: String = "",
    val available: Boolean = true,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)