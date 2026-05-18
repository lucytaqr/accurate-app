package com.accurate.userdirectory.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accurate.userdirectory.core.common.UiText
import com.accurate.userdirectory.domain.repository.ActivityLogRepository
import com.accurate.userdirectory.domain.usecase.ObserveUsersUseCase
import com.accurate.userdirectory.domain.usecase.RefreshCitiesUseCase
import com.accurate.userdirectory.domain.usecase.RefreshUsersUseCase
import com.accurate.userdirectory.domain.usecase.SyncPendingUsersUseCase
import com.accurate.userdirectory.presentation.users.UserListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val refreshUsersUseCase: RefreshUsersUseCase,
    private val refreshCitiesUseCase: RefreshCitiesUseCase,
    private val syncPendingUsersUseCase: SyncPendingUsersUseCase,
    private val observeUsersUseCase: ObserveUsersUseCase,
    private val activityLogRepository: ActivityLogRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeUsersUseCase().collect { users ->
                val pendingCount = users.count { it.syncStatus.name == "PendingCreate" }
                _state.update { it.copy(pendingSyncCount = pendingCount) }
            }
        }
    }

    fun onRefreshData() {
        viewModelScope.launch {
            _state.update { it.copy(isSyncing = true) }
            refreshCitiesUseCase()
            refreshUsersUseCase()
            val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
            _state.update {
                it.copy(
                    isSyncing = false,
                    lastSyncText = "Last sync: ${formatter.format(Date())}",
                    message = UiText.success("Data berhasil diperbarui")
                )
            }
            activityLogRepository.addLog("settings_refresh", "Manual Refresh", "Data diperbarui dari Settings")
        }
    }

    fun onSyncPending() {
        viewModelScope.launch {
            _state.update { it.copy(isSyncing = true) }
            val result = syncPendingUsersUseCase()
            result.fold(
                onSuccess = { count ->
                    _state.update {
                        it.copy(
                            isSyncing = false,
                            pendingSyncCount = 0,
                            message = UiText.success("$count user berhasil disinkronkan")
                        )
                    }
                    activityLogRepository.addLog("sync", "Sync Berhasil", "$count pending user berhasil disinkronkan")
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            isSyncing = false,
                            message = UiText.error(e.message ?: "Gagal sinkronisasi")
                        )
                    }
                }
            )
        }
    }

    fun onClearMessage() {
        _state.update { it.copy(message = null) }
    }
}
