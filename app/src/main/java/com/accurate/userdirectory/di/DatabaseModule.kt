package com.accurate.userdirectory.di

import android.content.Context
import androidx.room.Room
import com.accurate.userdirectory.core.database.AccurateDatabase
import com.accurate.userdirectory.data.local.dao.ActivityLogDao
import com.accurate.userdirectory.data.local.dao.CityDao
import com.accurate.userdirectory.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AccurateDatabase =
        Room.databaseBuilder(
            context,
            AccurateDatabase::class.java,
            "accurate_directory.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideUserDao(database: AccurateDatabase): UserDao = database.userDao()

    @Provides
    fun provideCityDao(database: AccurateDatabase): CityDao = database.cityDao()

    @Provides
    fun provideActivityLogDao(database: AccurateDatabase): ActivityLogDao = database.activityLogDao()
}
