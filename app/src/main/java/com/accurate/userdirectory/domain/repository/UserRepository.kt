package com.accurate.userdirectory.domain.repository

import com.accurate.userdirectory.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUsers(): Flow<List<User>>
    suspend fun getUserById(localId: String): User?
    suspend fun refreshUsers(): Result<Unit>
    suspend fun addUser(
        name: String,
        email: String,
        phoneNumber: String,
        address: String,
        city: String,
        genderApiValue: Int,
        photoUri: String?
    ): Result<User>
    suspend fun updateUser(
        localId: String,
        name: String,
        email: String,
        phoneNumber: String,
        address: String,
        city: String,
        genderApiValue: Int,
        photoUri: String?
    ): Result<User>
    suspend fun deleteUser(localId: String): Result<Unit>
    suspend fun syncPendingUsers(): Result<Int>
}
