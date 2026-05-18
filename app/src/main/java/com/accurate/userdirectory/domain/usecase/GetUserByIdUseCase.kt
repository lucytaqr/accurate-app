package com.accurate.userdirectory.domain.usecase

import com.accurate.userdirectory.domain.model.User
import com.accurate.userdirectory.domain.repository.UserRepository
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(localId: String): User? = userRepository.getUserById(localId)
}
