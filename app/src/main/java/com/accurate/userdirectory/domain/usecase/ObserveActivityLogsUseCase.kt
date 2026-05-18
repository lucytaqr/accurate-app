package com.accurate.userdirectory.domain.usecase

import com.accurate.userdirectory.domain.model.ActivityLog
import com.accurate.userdirectory.domain.repository.ActivityLogRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveActivityLogsUseCase @Inject constructor(
    private val activityLogRepository: ActivityLogRepository
) {
    operator fun invoke(): Flow<List<ActivityLog>> = activityLogRepository.observeLogs()
}
