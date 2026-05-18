package com.accurate.userdirectory.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.accurate.userdirectory.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun observeUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE syncStatus = 'PendingCreate'")
    suspend fun getPendingUsers(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUsers(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("UPDATE users SET remoteId = :remoteId, syncStatus = :syncStatus, updatedAt = :updatedAt WHERE localId = :localId")
    suspend fun updateSyncStatus(localId: String, remoteId: String?, syncStatus: String, updatedAt: Long)

    @Query("DELETE FROM users WHERE syncStatus = 'Synced'")
    suspend fun deleteSyncedUsers()
}
