package com.accurate.userdirectory.core.common

interface DateTimeProvider {
    fun currentTimeMillis(): Long
}

class SystemDateTimeProvider : DateTimeProvider {
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
}
