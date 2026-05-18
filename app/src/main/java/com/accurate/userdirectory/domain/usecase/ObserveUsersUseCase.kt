package com.accurate.userdirectory.domain.usecase

import com.accurate.userdirectory.domain.model.User
import com.accurate.userdirectory.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<List<User>> = userRepository.observeUsers()
}
