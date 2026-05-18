package com.accurate.userdirectory.data.repository

import com.accurate.userdirectory.core.network.ApiErrorHandler
import com.accurate.userdirectory.core.network.NetworkMonitor
import com.accurate.userdirectory.data.local.dao.UserDao
import com.accurate.userdirectory.data.mapper.createPendingUserEntity
import com.accurate.userdirectory.data.mapper.toCreateRequestDto
import com.accurate.userdirectory.data.mapper.toDomain
import com.accurate.userdirectory.data.mapper.toEntity
import com.accurate.userdirectory.data.remote.api.AccurateApiService
import com.accurate.userdirectory.data.remote.dto.CreateUserRequestDto
import com.accurate.userdirectory.domain.model.SyncStatus
import com.accurate.userdirectory.domain.model.User
import com.accurate.userdirectory.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val apiService: AccurateApiService,
    private val userDao: UserDao,
    private val networkMonitor: NetworkMonitor
) : UserRepository {

    override fun observeUsers(): Flow<List<User>> =
        userDao.observeUsers().map { entities -> entities.map { it.toDomain() } }

    override suspend fun refreshUsers(): Result<Unit> = runCatching {
        val dtos = apiService.getUsers()
        val entities = dtos.map { it.toEntity() }
        val existingPending = userDao.getPendingUsers()
        userDao.deleteSyncedUsers()
        userDao.upsertUsers(entities + existingPending)
    }

    override suspend fun addUser(
        name: String,
        email: String,
        phoneNumber: String,
        address: String,
        city: String,
        genderApiValue: Int,
        photoUri: String?
    ): Result<User> {
        val isOnline = networkMonitor.isOnline.first()

        return if (isOnline) {
            runCatching {
                val requestDto = CreateUserRequestDto(
                    name = name,
                    address = address,
                    email = email,
                    phoneNumber = phoneNumber,
                    city = city,
                    gender = genderApiValue,
                    photoUri = photoUri
                )
                val responseDto = apiService.createUser(requestDto)
                val entity = responseDto.toEntity().copy(photoUri = photoUri)
                userDao.insertUser(entity)
                entity.toDomain()
            }
        } else {
            runCatching {
                val pendingEntity = createPendingUserEntity(
                    name = name,
                    email = email,
                    phoneNumber = phoneNumber,
                    address = address,
                    city = city,
                    genderApiValue = genderApiValue,
                    photoUri = photoUri
                )
                userDao.insertUser(pendingEntity)
                pendingEntity.toDomain()
            }
        }
    }

    override suspend fun syncPendingUsers(): Result<Int> {
        var syncedCount = 0
        return runCatching {
            val pendingUsers = userDao.getPendingUsers()
            pendingUsers.forEach { pending ->
                try {
                    val requestDto = pending.toDomain().toCreateRequestDto()
                    val responseDto = apiService.createUser(requestDto)
                    val now = System.currentTimeMillis()
                    userDao.updateSyncStatus(
                        localId = pending.localId,
                        remoteId = responseDto.id ?: pending.localId,
                        syncStatus = SyncStatus.Synced.name,
                        updatedAt = now
                    )
                    syncedCount++
                } catch (e: Exception) {
                    Timber.e(e, "Failed to sync user ${pending.localId}")
                }
            }
            syncedCount
        }
    }
}
