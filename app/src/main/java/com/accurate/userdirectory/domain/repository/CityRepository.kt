package com.accurate.userdirectory.domain.repository

import com.accurate.userdirectory.domain.model.City
import kotlinx.coroutines.flow.Flow

interface CityRepository {
    fun observeCities(): Flow<List<City>>
    suspend fun refreshCities(): Result<Unit>
}
