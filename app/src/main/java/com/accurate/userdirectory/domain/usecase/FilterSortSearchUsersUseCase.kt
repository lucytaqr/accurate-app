package com.accurate.userdirectory.domain.usecase

import com.accurate.userdirectory.domain.model.Gender
import com.accurate.userdirectory.domain.model.User
import com.accurate.userdirectory.domain.model.UserFilter
import com.accurate.userdirectory.domain.model.SortOption
import javax.inject.Inject

class FilterSortSearchUsersUseCase @Inject constructor() {

    operator fun invoke(users: List<User>, filter: UserFilter): List<User> {
        val filtered = users.filter { user ->
            val matchesKeyword = filter.keyword.isBlank() ||
                user.name.contains(filter.keyword, ignoreCase = true) ||
                user.email.contains(filter.keyword, ignoreCase = true) ||
                user.city.contains(filter.keyword, ignoreCase = true)

            val matchesCity = filter.selectedCities.isEmpty() || user.city in filter.selectedCities
            val matchesGender = filter.selectedGender == null || user.gender == filter.selectedGender

            matchesKeyword && matchesCity && matchesGender
        }

        return when (filter.sortOption) {
            SortOption.NameAsc -> filtered.sortedBy { it.name.lowercase() }
            SortOption.NameDesc -> filtered.sortedByDescending { it.name.lowercase() }
        }
    }
}
