package com.accurate.userdirectory.data.mapper

import com.accurate.userdirectory.data.local.entity.CityEntity
import com.accurate.userdirectory.data.remote.dto.CityDto
import com.accurate.userdirectory.domain.model.City

fun CityDto.toEntity(): CityEntity {
    val cityName = name?.takeIf { it.isNotBlank() }
        ?: city?.takeIf { it.isNotBlank() }
        ?: return CityEntity(name = "Unknown", updatedAt = System.currentTimeMillis())

    return CityEntity(
        name = cityName,
        updatedAt = System.currentTimeMillis()
    )
}

fun CityEntity.toDomain(): City = City(name = name)
