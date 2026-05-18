package com.accurate.userdirectory.domain.model

data class User(
    val id: String,
    val remoteId: String?,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val city: String,
    val gender: Gender,
    val photoUri: String?,
    val syncStatus: SyncStatus,
    val createdAt: Long,
    val updatedAt: Long
)
