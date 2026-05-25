package com.accurate.userdirectory.data.repository

import com.accurate.userdirectory.core.network.NetworkMonitor
import retrofit2.HttpException
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

    override suspend fun getUserById(localId: String): User? =
        userDao.observeUsers().first().find { it.localId == localId }?.toDomain()

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
                try {
                    val responseDto = apiService.createUser(requestDto)
                    val entity = responseDto.toEntity().copy(photoUri = photoUri)
                    userDao.insertUser(entity)
                    entity.toDomain()
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    throw Exception(errorBody ?: e.message)
                }
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

    override suspend fun updateUser(
        localId: String,
        name: String,
        email: String,
        phoneNumber: String,
        address: String,
        city: String,
        genderApiValue: Int,
        photoUri: String?
    ): Result<User> {
        val isOnline = networkMonitor.isOnline.first()
        val user = getUserById(localId) ?: return Result.failure(Exception("User not found"))

        return if (isOnline && user.remoteId != null) {
            runCatching {
                val requestDto = CreateUserRequestDto(name, address, email, phoneNumber, city, genderApiValue, photoUri)
                val responseDto = apiService.updateUser(user.remoteId!!, requestDto)
                val now = System.currentTimeMillis()
                userDao.updateUser(localId, name, email, phoneNumber, address, city, genderApiValue, photoUri ?: user.photoUri, now)
                userDao.updateSyncStatus(localId, responseDto.id, SyncStatus.Synced.name, now)
                getUserById(localId)!!
            }
        } else {
            runCatching {
                val now = System.currentTimeMillis()
                userDao.updateUser(localId, name, email, phoneNumber, address, city, genderApiValue, photoUri ?: user.photoUri, now)
                if (user.syncStatus == SyncStatus.PendingCreate) {
                    userDao.updateSyncStatus(localId, null, SyncStatus.PendingCreate.name, now)
                }
                getUserById(localId)!!
            }
        }
    }

    override suspend fun deleteUser(localId: String): Result<Unit> = runCatching {
        val user = getUserById(localId)
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline && user?.remoteId != null) {
            runCatching { apiService.deleteUser(user.remoteId!!) }
        }
        userDao.deleteUser(localId)
    }
}
