package com.accurate.userdirectory.data.mapper

import com.accurate.userdirectory.data.local.entity.UserEntity
import com.accurate.userdirectory.data.remote.dto.CreateUserRequestDto
import com.accurate.userdirectory.data.remote.dto.UserDto
import com.accurate.userdirectory.domain.model.Gender
import com.accurate.userdirectory.domain.model.SyncStatus
import com.accurate.userdirectory.domain.model.User
import java.util.UUID

fun UserDto.toEntity(): UserEntity {
    val now = System.currentTimeMillis()
    return UserEntity(
        localId = id ?: UUID.randomUUID().toString(),
        remoteId = id,
        name = name.orEmpty(),
        email = email.orEmpty(),
        phoneNumber = phoneNumber.orEmpty(),
        address = address.orEmpty(),
        city = city.orEmpty(),
        gender = gender ?: 0,
        photoUri = photoUri ?: photoUrl,
        syncStatus = SyncStatus.Synced.name,
        createdAt = now,
        updatedAt = now
    )
}

fun UserEntity.toDomain(): User = User(
    id = localId,
    remoteId = remoteId,
    name = name,
    email = email,
    phoneNumber = phoneNumber,
    address = address,
    city = city,
    gender = Gender.fromApiValue(gender),
    photoUri = photoUri,
    syncStatus = try { SyncStatus.valueOf(syncStatus) } catch (_: Exception) { SyncStatus.Synced },
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun User.toEntity(): UserEntity = UserEntity(
    localId = id,
    remoteId = remoteId,
    name = name,
    email = email,
    phoneNumber = phoneNumber,
    address = address,
    city = city,
    gender = gender.apiValue,
    photoUri = photoUri,
    syncStatus = syncStatus.name,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun createPendingUserEntity(
    name: String,
    email: String,
    phoneNumber: String,
    address: String,
    city: String,
    genderApiValue: Int,
    photoUri: String?
): UserEntity {
    val now = System.currentTimeMillis()
    return UserEntity(
        localId = UUID.randomUUID().toString(),
        remoteId = null,
        name = name,
        email = email,
        phoneNumber = phoneNumber,
        address = address,
        city = city,
        gender = genderApiValue,
        photoUri = photoUri,
        syncStatus = SyncStatus.PendingCreate.name,
        createdAt = now,
        updatedAt = now
    )
}

fun User.toCreateRequestDto(): CreateUserRequestDto = CreateUserRequestDto(
    name = name,
    address = address,
    email = email,
    phoneNumber = phoneNumber,
    city = city,
    gender = gender.apiValue,
    photoUri = photoUri
)
