package com.accurate.userdirectory.presentation.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accurate.userdirectory.core.common.UiText
import com.accurate.userdirectory.core.network.NetworkMonitor
import com.accurate.userdirectory.domain.model.Gender
import com.accurate.userdirectory.domain.model.City
import com.accurate.userdirectory.domain.model.SortOption
import com.accurate.userdirectory.domain.model.User
import com.accurate.userdirectory.domain.model.UserFilter
import com.accurate.userdirectory.domain.repository.ActivityLogRepository
import com.accurate.userdirectory.domain.usecase.DeleteUserUseCase
import com.accurate.userdirectory.domain.usecase.FilterSortSearchUsersUseCase
import com.accurate.userdirectory.domain.usecase.ObserveCitiesUseCase
import com.accurate.userdirectory.domain.usecase.ObserveUsersUseCase
import com.accurate.userdirectory.domain.usecase.RefreshCitiesUseCase
import com.accurate.userdirectory.domain.usecase.RefreshUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val observeUsersUseCase: ObserveUsersUseCase,
    private val refreshUsersUseCase: RefreshUsersUseCase,
    private val observeCitiesUseCase: ObserveCitiesUseCase,
    private val refreshCitiesUseCase: RefreshCitiesUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val networkMonitor: NetworkMonitor,
    private val activityLogRepository: ActivityLogRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UserListUiState())
    val state: StateFlow<UserListUiState> = _state.asStateFlow()

    private var observeJob: Job? = null

    init {
        observeJob = viewModelScope.launch {
            combine(
                observeUsersUseCase(),
                observeCitiesUseCase(),
                networkMonitor.isOnline
            ) { users, cities, isOnline ->
                applyFilter(users, _state.value.filter, cities, isOnline)
            }.collect { newState ->
                _state.value = newState
            }
        }
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isInitialLoading = true) }
            refreshCitiesUseCase()
            refreshUsersUseCase()
            _state.update { it.copy(isInitialLoading = false, lastUpdatedText = getCurrentTimeText()) }
        }
    }

    fun onSearchChanged(query: String) {
        val currentState = _state.value
        val newFilter = currentState.filter.copy(keyword = query)
        _state.update { it.copy(filter = newFilter) }
        updateDisplayedUsers()
    }

    fun onSortChanged(sortOption: SortOption) {
        val currentState = _state.value
        val newFilter = currentState.filter.copy(sortOption = sortOption)
        _state.update { it.copy(filter = newFilter) }
        updateDisplayedUsers()
    }

    fun onFilterClicked() {
        val currentState = _state.value
        _state.update { it.copy(tempFilter = currentState.filter, showFilterSheet = true) }
    }

    fun onFilterDismissed() {
        _state.update { it.copy(showFilterSheet = false) }
    }

    fun onTempFilterChanged(tempFilter: UserFilter) {
        _state.update { it.copy(tempFilter = tempFilter) }
    }

    fun onApplyFilter() {
        val currentState = _state.value
        _state.update { it.copy(filter = currentState.tempFilter, showFilterSheet = false) }
        updateDisplayedUsers()
    }

    fun onResetFilter() {
        val resetFilter = UserFilter()
        _state.update { it.copy(filter = resetFilter, tempFilter = resetFilter) }
        updateDisplayedUsers()
    }

    fun onRemoveCityFilter(city: String) {
        val currentState = _state.value
        val newCities = currentState.filter.selectedCities - city
        val newFilter = currentState.filter.copy(selectedCities = newCities)
        _state.update { it.copy(filter = newFilter, tempFilter = newFilter) }
        updateDisplayedUsers()
    }

    fun onRemoveGenderFilter() {
        val currentState = _state.value
        val newFilter = currentState.filter.copy(selectedGender = null)
        _state.update { it.copy(filter = newFilter, tempFilter = newFilter) }
        updateDisplayedUsers()
    }

    fun onRefresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            refreshCitiesUseCase()
            val result = refreshUsersUseCase()
            if (result.isSuccess) {
                activityLogRepository.addLog("refresh", "Data Refreshed", "Daftar user berhasil diperbarui")
            } else {
                val error = result.exceptionOrNull()?.message ?: "Gagal refresh data"
                if (_state.value.users.isEmpty()) {
                    _state.update { it.copy(errorMessage = UiText.error(error)) }
                }
            }
            _state.update { it.copy(isRefreshing = false, lastUpdatedText = getCurrentTimeText()) }
        }
    }

    fun onRetry() {
        _state.update { it.copy(errorMessage = null) }
        loadData()
    }

    fun onClearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun onEditUser(userId: String) {
        _state.update { it.copy(actionMessage = UiText("edit:$userId")) }
    }

    fun onShowDeleteDialog(user: User) {
        _state.update { it.copy(showDeleteDialog = true, deleteTargetUser = user) }
    }

    fun onDismissDeleteDialog() {
        _state.update { it.copy(showDeleteDialog = false, deleteTargetUser = null) }
    }

    fun onConfirmDelete() {
        val target = _state.value.deleteTargetUser ?: return
        _state.update { it.copy(showDeleteDialog = false) }
        viewModelScope.launch {
            val result = deleteUserUseCase(target.id)
            result.fold(
                onSuccess = {
                    activityLogRepository.addLog("delete", "User Dihapus", "User ${target.name} berhasil dihapus")
                    _state.update { it.copy(actionMessage = UiText.success("${target.name} dihapus")) }
                },
                onFailure = { e ->
                    _state.update { it.copy(actionMessage = UiText.error(e.message ?: "Gagal menghapus user")) }
                }
            )
        }
    }

    fun onClearActionMessage() {
        _state.update { it.copy(actionMessage = null) }
    }

    private fun updateDisplayedUsers() {
        val currentState = _state.value
        applyFilter(currentState.users, currentState.filter, currentState.cities, currentState.isOffline)
            .let { _state.value = it }
    }

    private fun applyFilter(
        users: List<User>,
        filter: UserFilter,
        cities: List<City>,
        isOffline: Boolean
    ): UserListUiState {
        val filtered = users.filter { user ->
            val matchesKeyword = filter.keyword.isBlank() ||
                user.name.contains(filter.keyword, ignoreCase = true) ||
                user.email.contains(filter.keyword, ignoreCase = true) ||
                user.city.contains(filter.keyword, ignoreCase = true)

            val matchesCity = filter.selectedCities.isEmpty() || user.city in filter.selectedCities
            val matchesGender = filter.selectedGender == null || user.gender == filter.selectedGender

            matchesKeyword && matchesCity && matchesGender
        }

        val sorted = when (filter.sortOption) {
            SortOption.NameAsc -> filtered.sortedBy { it.name.lowercase() }
            SortOption.NameDesc -> filtered.sortedByDescending { it.name.lowercase() }
        }

        val pendingCount = users.count { it.syncStatus.name == "PendingCreate" }

        return UserListUiState(
            isInitialLoading = false,
            isRefreshing = false,
            isOffline = isOffline,
            users = users,
            displayedUsers = sorted,
            cities = cities,
            filter = filter,
            tempFilter = filter,
            lastUpdatedText = _state.value.lastUpdatedText,
            pendingSyncCount = pendingCount,
            totalUserCount = users.size,
            displayedUserCount = sorted.size,
            isEmpty = users.isEmpty(),
            isEmptyFilterResult = filtered.isEmpty() && users.isNotEmpty(),
            errorMessage = null
        )
    }

    private fun getCurrentTimeText(): String {
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        return "Last update: ${formatter.format(Date())}"
    }
}
