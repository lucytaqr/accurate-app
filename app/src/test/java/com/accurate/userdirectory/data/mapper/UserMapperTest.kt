package com.accurate.userdirectory.data.mapper

import com.accurate.userdirectory.data.local.entity.UserEntity
import com.accurate.userdirectory.data.remote.dto.UserDto
import com.accurate.userdirectory.domain.model.Gender
import com.accurate.userdirectory.domain.model.SyncStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class UserMapperTest {

    @Test
    fun `dto to entity maps fields correctly`() {
        val dto = UserDto(
            id = "123",
            name = "Test User",
            address = "Jl Test",
            email = "test@email.com",
            phoneNumber = "08123456789",
            city = "Jakarta",
            gender = 0
        )
        val entity = dto.toEntity()

        assertEquals("123", entity.remoteId)
        assertEquals("123", entity.localId)
        assertEquals("Test User", entity.name)
        assertEquals("test@email.com", entity.email)
        assertEquals("08123456789", entity.phoneNumber)
        assertEquals("Jl Test", entity.address)
        assertEquals("Jakarta", entity.city)
        assertEquals(0, entity.gender)
        assertEquals(SyncStatus.Synced.name, entity.syncStatus)
    }

    @Test
    fun `dto with null fields maps to empty defaults`() {
        val dto = UserDto(
            id = "1",
            name = null,
            address = null,
            email = null,
            phoneNumber = null,
            city = null,
            gender = null
        )
        val entity = dto.toEntity()

        assertEquals("", entity.name)
        assertEquals("", entity.email)
        assertEquals("", entity.phoneNumber)
        assertEquals("", entity.address)
        assertEquals("", entity.city)
        assertEquals(0, entity.gender)
    }

    @Test
    fun `entity to domain maps correctly`() {
        val entity = UserEntity(
            localId = "local-1",
            remoteId = "remote-1",
            name = "Test",
            email = "test@email.com",
            phoneNumber = "081",
            address = "Jl A",
            city = "Jakarta",
            gender = 1,
            photoUri = "content://photo",
            syncStatus = "Synced",
            createdAt = 1000,
            updatedAt = 2000
        )
        val user = entity.toDomain()

        assertEquals("local-1", user.id)
        assertEquals("remote-1", user.remoteId)
        assertEquals("Test", user.name)
        assertEquals(Gender.Female, user.gender)
        assertEquals("content://photo", user.photoUri)
        assertEquals(SyncStatus.Synced, user.syncStatus)
        assertEquals(1000, user.createdAt)
        assertEquals(2000, user.updatedAt)
    }

    @Test
    fun `gender from api value 0 maps to male`() {
        val dto = UserDto(id = "1", gender = 0)
        val entity = dto.toEntity()
        val user = entity.toDomain()
        assertEquals(Gender.Male, user.gender)
    }

    @Test
    fun `gender from api value 1 maps to female`() {
        val dto = UserDto(id = "1", gender = 1)
        val entity = dto.toEntity()
        val user = entity.toDomain()
        assertEquals(Gender.Female, user.gender)
    }

    @Test
    fun `unknown gender maps to male fallback`() {
        val entity = UserEntity(
            localId = "1", remoteId = "1", name = "X", email = "x@x.com",
            phoneNumber = "0", address = "", city = "",
            gender = 99, photoUri = null, syncStatus = "Synced",
            createdAt = 0, updatedAt = 0
        )
        val user = entity.toDomain()
        assertEquals(Gender.Male, user.gender)
    }
}
