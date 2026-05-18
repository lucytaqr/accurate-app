package com.accurate.userdirectory.domain.model

data class ActivityLog(
    val id: String,
    val type: String,
    val title: String,
    val description: String,
    val createdAt: Long
)
