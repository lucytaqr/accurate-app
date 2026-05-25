package com.accurate.userdirectory.data.repository

import com.accurate.userdirectory.data.local.dao.CityDao
import com.accurate.userdirectory.data.local.dao.UserDao
import com.accurate.userdirectory.data.local.entity.CityEntity
import com.accurate.userdirectory.data.mapper.toDomain
import com.accurate.userdirectory.data.mapper.toEntity
import com.accurate.userdirectory.data.remote.api.AccurateApiService
import com.accurate.userdirectory.domain.model.City
import com.accurate.userdirectory.domain.repository.CityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CityRepositoryImpl @Inject constructor(
    private val apiService: AccurateApiService,
    private val cityDao: CityDao,
    private val userDao: UserDao
) : CityRepository {

    override fun observeCities(): Flow<List<City>> =
        cityDao.observeCities().map { entities -> entities.map { it.toDomain() } }

    override suspend fun refreshCities(): Result<Unit> = runCatching {
        try {
            val dtos = apiService.getCities()
            val entities = dtos.mapNotNull { dto ->
                val entity = dto.toEntity()
                if (entity.name != "Unknown") entity else null
            }
            cityDao.deleteAllCities()
            cityDao.upsertCities(entities)
        } catch (e: Exception) {
            val users = userDao.observeUsers().first()
            val uniqueCities = users.map { it.city }.filter { it.isNotBlank() }.distinct().sorted()
            val cityEntities = uniqueCities.map { CityEntity(name = it, updatedAt = System.currentTimeMillis()) }
            cityDao.deleteAllCities()
            cityDao.upsertCities(cityEntities)
        }
    }
}
