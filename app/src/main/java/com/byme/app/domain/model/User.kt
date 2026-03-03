package com.byme.app.domain.model

import com.google.firebase.firestore.PropertyName

data class User(
    val id: String = "",
    val name: String = "",
    val lastname: String = "",
    val email: String = "",
    val phone: String = "",
    val photoUrl: String = "",
    @get:PropertyName("isProfessional")
    @set:PropertyName("isProfessional")
    var isProfessional: Boolean = false,
    val role: String = "user",
    val createdAt: Long = System.currentTimeMillis(),
    // Campos profesionales (vacíos por defecto, porque el usuario no es profesional al crear su cuenta)
    val category: String = "",
    val description: String = "",
    val experience: String = "",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val available: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)
