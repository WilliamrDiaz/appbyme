package com.byme.app.data.local

import com.byme.app.data.local.entity.UserEntity
import com.byme.app.domain.model.User

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        lastname = lastname,
        email = email,
        phone = phone,
        photoUrl = photoUrl,
        isProfessional = isProfessional,
        role = role,
        createdAt = createdAt,
        category = category,
        description = description,
        experience = experience,
        rating = rating,
        reviewCount = reviewCount,
        available = available,
        latitude = latitude,
        longitude = longitude
    )
}

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        name = name,
        lastname = lastname,
        email = email,
        phone = phone,
        photoUrl = photoUrl,
        isProfessional = isProfessional,
        role = role,
        createdAt = createdAt,
        category = category,
        description = description,
        experience = experience,
        rating = rating,
        reviewCount = reviewCount,
        available = available,
        latitude = latitude,
        longitude = longitude
    )
}