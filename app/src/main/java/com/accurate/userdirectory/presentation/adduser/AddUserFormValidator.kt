package com.accurate.userdirectory.presentation.adduser

import com.accurate.userdirectory.domain.model.Gender

object AddUserFormValidator {
    fun validate(state: AddUserUiState): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (state.name.isBlank()) {
            errors["name"] = "Nama tidak boleh kosong"
        }

        if (state.email.isBlank()) {
            errors["email"] = "Email tidak boleh kosong"
        } else if (!isValidEmail(state.email)) {
            errors["email"] = "Format email tidak valid"
        }

        if (state.phoneNumber.isBlank()) {
            errors["phoneNumber"] = "No. handphone tidak boleh kosong"
        }

        if (state.city.isBlank()) {
            errors["city"] = "Kota harus dipilih"
        }

        if (state.gender == null) {
            errors["gender"] = "Jenis kelamin harus dipilih"
        }

        return errors
    }

    private fun isValidEmail(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
    }
}
