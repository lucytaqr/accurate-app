package com.accurate.userdirectory.presentation.adduser

import com.accurate.userdirectory.core.common.UiText
import com.accurate.userdirectory.domain.model.City
import com.accurate.userdirectory.domain.model.Gender

data class AddUserUiState(
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val city: String = "",
    val gender: Gender? = null,
    val photoUri: String? = null,
    val cities: List<City> = emptyList(),
    val fieldErrors: Map<String, String> = emptyMap(),
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val message: UiText? = null
)
