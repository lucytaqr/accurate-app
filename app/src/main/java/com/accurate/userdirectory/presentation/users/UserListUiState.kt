package com.accurate.userdirectory.presentation.users

import com.accurate.userdirectory.core.common.UiText
import com.accurate.userdirectory.domain.model.City
import com.accurate.userdirectory.domain.model.User
import com.accurate.userdirectory.domain.model.UserFilter

data class UserListUiState(
    val isInitialLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isOffline: Boolean = false,
    val users: List<User> = emptyList(),
    val displayedUsers: List<User> = emptyList(),
    val cities: List<City> = emptyList(),
    val filter: UserFilter = UserFilter(),
    val tempFilter: UserFilter = UserFilter(),
    val showFilterSheet: Boolean = false,
    val lastUpdatedText: String = "-",
    val pendingSyncCount: Int = 0,
    val totalUserCount: Int = 0,
    val displayedUserCount: Int = 0,
    val errorMessage: UiText? = null,
    val isEmpty: Boolean = false,
    val isEmptyFilterResult: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val deleteTargetUser: User? = null,
    val actionMessage: UiText? = null
)
