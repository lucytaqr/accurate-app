package com.accurate.userdirectory.di

import android.content.Context
import com.accurate.userdirectory.analytics.AnalyticsHelper
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics =
        FirebaseAnalytics.getInstance(context)

    @Provides
    @Singleton
    fun provideAnalyticsHelper(firebaseAnalytics: FirebaseAnalytics): AnalyticsHelper =
        AnalyticsHelper(firebaseAnalytics)
}
