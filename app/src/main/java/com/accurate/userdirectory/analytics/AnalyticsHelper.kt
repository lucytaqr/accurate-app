package com.accurate.userdirectory.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsHelper @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {
    fun logEvent(event: String, params: Map<String, String> = emptyMap()) {
        val bundle = Bundle().apply {
            params.forEach { (key, value) -> putString(key, value) }
        }
        firebaseAnalytics.logEvent(event, bundle)
    }

    fun logAppOpened() = logEvent("app_opened")
    fun logUserListViewed() = logEvent("user_list_viewed")
    fun logUserSearchUsed() = logEvent("user_search_used")
    fun logFilterApplied(filterType: String) = logEvent("filter_applied", mapOf("filter_type" to filterType))
    fun logSortChanged(sortOption: String) = logEvent("sort_changed", mapOf("sort_option" to sortOption))
    fun logAddUserSubmitted() = logEvent("add_user_submitted")
    fun logAddUserSuccess() = logEvent("add_user_success")
    fun logAddUserFailed(reason: String) = logEvent("add_user_failed", mapOf("reason" to reason))
    fun logOfflineModeShown() = logEvent("offline_mode_shown")
    fun logSyncTriggered() = logEvent("sync_triggered")
    fun logSyncSuccess(count: Int) = logEvent("sync_success", mapOf("count" to count.toString()))
}
