package com.accurate.userdirectory.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "localId")
    val localId: String,

    @ColumnInfo(name = "remoteId")
    val remoteId: String?,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "phoneNumber")
    val phoneNumber: String,

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "city")
    val city: String,

    @ColumnInfo(name = "gender")
    val gender: Int,

    @ColumnInfo(name = "photoUri")
    val photoUri: String?,

    @ColumnInfo(name = "syncStatus")
    val syncStatus: String,

    @ColumnInfo(name = "createdAt")
    val createdAt: Long,

    @ColumnInfo(name = "updatedAt")
    val updatedAt: Long
)
