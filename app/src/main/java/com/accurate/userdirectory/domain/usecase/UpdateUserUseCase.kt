package com.accurate.userdirectory.domain.usecase

import com.accurate.userdirectory.domain.model.User
import com.accurate.userdirectory.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        localId: String,
        name: String,
        email: String,
        phoneNumber: String,
        address: String,
        city: String,
        genderApiValue: Int,
        photoUri: String?
    ): Result<User> = userRepository.updateUser(localId, name, email, phoneNumber, address, city, genderApiValue, photoUri)
}
