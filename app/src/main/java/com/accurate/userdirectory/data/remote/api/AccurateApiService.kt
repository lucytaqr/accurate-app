package com.accurate.userdirectory.data.remote.api

import com.accurate.userdirectory.data.remote.dto.CityDto
import com.accurate.userdirectory.data.remote.dto.CreateUserRequestDto
import com.accurate.userdirectory.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AccurateApiService {
    @GET("api/v2/accurate/user")
    suspend fun getUsers(): List<UserDto>

    @POST("api/v2/accurate/user")
    suspend fun createUser(@Body user: CreateUserRequestDto): UserDto

    @GET("api/v2/accurate/city")
    suspend fun getCities(): List<CityDto>
}
