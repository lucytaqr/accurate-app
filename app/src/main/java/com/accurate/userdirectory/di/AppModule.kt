package com.accurate.userdirectory.di

import com.accurate.userdirectory.core.common.DateTimeProvider
import com.accurate.userdirectory.core.common.DefaultDispatchersProvider
import com.accurate.userdirectory.core.common.DispatchersProvider
import com.accurate.userdirectory.core.common.SystemDateTimeProvider
import com.accurate.userdirectory.core.network.AndroidNetworkMonitor
import com.accurate.userdirectory.core.network.NetworkMonitor
import com.accurate.userdirectory.data.repository.ActivityLogRepositoryImpl
import com.accurate.userdirectory.data.repository.CityRepositoryImpl
import com.accurate.userdirectory.data.repository.UserRepositoryImpl
import com.accurate.userdirectory.domain.repository.ActivityLogRepository
import com.accurate.userdirectory.domain.repository.CityRepository
import com.accurate.userdirectory.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindCityRepository(impl: CityRepositoryImpl): CityRepository

    @Binds
    @Singleton
    abstract fun bindActivityLogRepository(impl: ActivityLogRepositoryImpl): ActivityLogRepository

    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(impl: AndroidNetworkMonitor): NetworkMonitor

    @Binds
    @Singleton
    abstract fun bindDispatchersProvider(impl: DefaultDispatchersProvider): DispatchersProvider

    @Binds
    @Singleton
    abstract fun bindDateTimeProvider(impl: SystemDateTimeProvider): DateTimeProvider
}
