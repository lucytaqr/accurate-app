package com.accurate.userdirectory.core.common

data class UiText(
    val text: String,
    val isError: Boolean = false
) {
    companion object {
        val Empty = UiText("")
        fun error(text: String) = UiText(text, isError = true)
        fun success(text: String) = UiText(text, isError = false)
    }
}
