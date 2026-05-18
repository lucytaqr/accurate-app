package com.accurate.userdirectory.domain.model

data class UserFilter(
    val keyword: String = "",
    val selectedCities: Set<String> = emptySet(),
    val selectedGender: Gender? = null,
    val sortOption: SortOption = SortOption.NameAsc
) {
    val isActive: Boolean get() = keyword.isNotBlank() || selectedCities.isNotEmpty() || selectedGender != null
}
