package com.byme.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val lastname: String = "",
    val email: String = "",
    val phone: String = "",
    val photoUrl: String = "",
    val isProfessional: Boolean = false,
    val role: String = "user",
    val createdAt: Long = 0L,
    val category: String = "",
    val description: String = "",
    val experience: String = "",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val available: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
