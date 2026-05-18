package com.accurate.userdirectory.domain.usecase

import com.accurate.userdirectory.domain.repository.CityRepository
import javax.inject.Inject

class RefreshCitiesUseCase @Inject constructor(
    private val cityRepository: CityRepository
) {
    suspend operator fun invoke(): Result<Unit> = cityRepository.refreshCities()
}
