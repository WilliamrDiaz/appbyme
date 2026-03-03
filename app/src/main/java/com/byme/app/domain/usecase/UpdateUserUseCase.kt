package com.byme.app.domain.usecase

import com.byme.app.domain.model.User
import com.byme.app.domain.repository.UserRepositoryInterface

class UpdateUserUseCase(
    private val userRepository: UserRepositoryInterface,
) {
    suspend operator fun invoke(user: User): Result<Unit> {
        return userRepository.updateUser(user)
    }
}