package com.accurate.userdirectory.domain.usecase

import com.accurate.userdirectory.domain.repository.UserRepository
import javax.inject.Inject

class SyncPendingUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<Int> = userRepository.syncPendingUsers()
}
