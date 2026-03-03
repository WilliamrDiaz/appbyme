package com.byme.app.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class LoginUseCase(
    private val auth: FirebaseAuth,
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}