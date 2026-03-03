package com.byme.app.domain.usecase

import com.byme.app.domain.model.User
import com.byme.app.domain.repository.UserRepositoryInterface

class GetUserUseCase(
    private val userRepository: UserRepositoryInterface
) {
    suspend operator fun invoke(userId: String): Result<User> {
        return userRepository.getUser(userId)
    }
}