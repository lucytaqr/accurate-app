package com.accurate.userdirectory.domain.usecase

import com.accurate.userdirectory.domain.model.City
import com.accurate.userdirectory.domain.repository.CityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCitiesUseCase @Inject constructor(
    private val cityRepository: CityRepository
) {
    operator fun invoke(): Flow<List<City>> = cityRepository.observeCities()
}
