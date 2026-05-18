package com.accurate.userdirectory.domain.model

enum class Gender(val apiValue: Int, val displayName: String) {
    Male(0, "Male"),
    Female(1, "Female");

    companion object {
        fun fromApiValue(value: Int?): Gender = when (value) {
            1 -> Female
            else -> Male
        }
    }
}
