package com.accurate.userdirectory.presentation.activity

import com.accurate.userdirectory.domain.model.ActivityLog

data class ActivityUiState(
    val logs: List<ActivityLog> = emptyList(),
    val isLoading: Boolean = true
)
