package com.byme.app.domain.usecase

import com.byme.app.domain.model.User
import com.byme.app.domain.repository.UserRepositoryInterface

class GetProfessionalsUseCase(
    private val userRepository: UserRepositoryInterface
) {
    suspend operator fun invoke(): Result<List<User>> {
        return userRepository.getProfessionals()
    }
}