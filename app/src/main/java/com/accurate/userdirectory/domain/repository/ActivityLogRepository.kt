package com.accurate.userdirectory.domain.repository

import com.accurate.userdirectory.domain.model.ActivityLog
import kotlinx.coroutines.flow.Flow

interface ActivityLogRepository {
    fun observeLogs(): Flow<List<ActivityLog>>
    suspend fun addLog(type: String, title: String, description: String)
}
