package com.accurate.userdirectory.domain.usecase

import com.accurate.userdirectory.domain.repository.UserRepository
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(localId: String): Result<Unit> = userRepository.deleteUser(localId)
}
