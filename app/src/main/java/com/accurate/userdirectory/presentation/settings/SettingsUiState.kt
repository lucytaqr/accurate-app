package com.accurate.userdirectory.presentation.settings

import com.accurate.userdirectory.core.common.UiText

data class SettingsUiState(
    val appVersion: String = "1.0.0",
    val apiSource: String = "MockAPI",
    val lastSyncText: String = "-",
    val pendingSyncCount: Int = 0,
    val isSyncing: Boolean = false,
    val message: UiText? = null
)
