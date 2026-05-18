package com.accurate.userdirectory.data.repository

import com.accurate.userdirectory.data.local.dao.ActivityLogDao
import com.accurate.userdirectory.data.local.entity.ActivityLogEntity
import com.accurate.userdirectory.domain.model.ActivityLog
import com.accurate.userdirectory.domain.repository.ActivityLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityLogRepositoryImpl @Inject constructor(
    private val activityLogDao: ActivityLogDao
) : ActivityLogRepository {

    override fun observeLogs(): Flow<List<ActivityLog>> =
        activityLogDao.observeLogs().map { entities ->
            entities.map { entity ->
                ActivityLog(
                    id = entity.id,
                    type = entity.type,
                    title = entity.title,
                    description = entity.description,
                    createdAt = entity.createdAt
                )
            }
        }

    override suspend fun addLog(type: String, title: String, description: String) {
        val log = ActivityLogEntity(
            id = UUID.randomUUID().toString(),
            type = type,
            title = title,
            description = description,
            createdAt = System.currentTimeMillis()
        )
        activityLogDao.insertLog(log)
    }
}
