package com.byme.app.domain.usecase

import com.byme.app.domain.model.User
import com.byme.app.domain.repository.UserRepositoryInterface
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class RegisterUseCase(
    private val userRepository: UserRepositoryInterface,
    private val auth: FirebaseAuth,
) {

    suspend operator fun invoke(
        name: String,
        lastname: String,
        email: String,
        phone: String,
        password: String
    ): Result<Unit> {
        return try {
            // 1. Crear en Firebase Auth
            val authResult = auth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val userId = authResult.user?.uid
                ?: return Result.failure(Exception("Error al obtener usuario"))

            // 2. Guardar en Firestore
            val user = User(
                id = userId,
                name = name,
                lastname = lastname,
                email = email,
                phone = phone,
                isProfessional = false,
                role = "user"
            )
            userRepository.createUser(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}