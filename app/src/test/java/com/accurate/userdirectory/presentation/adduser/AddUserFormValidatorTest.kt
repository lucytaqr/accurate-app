package com.accurate.userdirectory.presentation.adduser

import com.accurate.userdirectory.domain.model.Gender
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AddUserFormValidatorTest {

    @Test
    fun `valid form returns no errors`() {
        val state = AddUserUiState(
            name = "Test User",
            email = "test@email.com",
            phoneNumber = "08123456789",
            address = "Jl Test",
            city = "Jakarta",
            gender = Gender.Male
        )
        val errors = AddUserFormValidator.validate(state)
        assertTrue(errors.isEmpty())
    }

    @Test
    fun `empty name returns error`() {
        val state = AddUserUiState(
            name = "",
            email = "test@email.com",
            phoneNumber = "08123456789",
            city = "Jakarta",
            gender = Gender.Male
        )
        val errors = AddUserFormValidator.validate(state)
        assertTrue(errors.containsKey("name"))
    }

    @Test
    fun `empty email returns error`() {
        val state = AddUserUiState(
            name = "Test",
            email = "",
            phoneNumber = "08123456789",
            city = "Jakarta",
            gender = Gender.Male
        )
        val errors = AddUserFormValidator.validate(state)
        assertTrue(errors.containsKey("email"))
    }

    @Test
    fun `invalid email format returns error`() {
        val state = AddUserUiState(
            name = "Test",
            email = "notanemail",
            phoneNumber = "08123456789",
            city = "Jakarta",
            gender = Gender.Male
        )
        val errors = AddUserFormValidator.validate(state)
        assertTrue(errors.containsKey("email"))
        assertEquals("Format email tidak valid", errors["email"])
    }

    @Test
    fun `empty phone returns error`() {
        val state = AddUserUiState(
            name = "Test",
            email = "test@email.com",
            phoneNumber = "",
            city = "Jakarta",
            gender = Gender.Male
        )
        val errors = AddUserFormValidator.validate(state)
        assertTrue(errors.containsKey("phoneNumber"))
    }

    @Test
    fun `empty city returns error`() {
        val state = AddUserUiState(
            name = "Test",
            email = "test@email.com",
            phoneNumber = "08123456789",
            city = "",
            gender = Gender.Male
        )
        val errors = AddUserFormValidator.validate(state)
        assertTrue(errors.containsKey("city"))
    }

    @Test
    fun `null gender returns error`() {
        val state = AddUserUiState(
            name = "Test",
            email = "test@email.com",
            phoneNumber = "08123456789",
            city = "Jakarta",
            gender = null
        )
        val errors = AddUserFormValidator.validate(state)
        assertTrue(errors.containsKey("gender"))
    }

    @Test
    fun `valid email formats are accepted`() {
        val validEmails = listOf(
            "user@domain.com",
            "user.name@domain.co.id",
            "user+tag@domain.org"
        )
        validEmails.forEach { email ->
            val state = AddUserUiState(
                name = "Test",
                email = email,
                phoneNumber = "08123456789",
                city = "Jakarta",
                gender = Gender.Male
            )
            val errors = AddUserFormValidator.validate(state)
            assertTrue("Email $email should be valid", !errors.containsKey("email"))
        }
    }

    @Test
    fun `multiple errors are returned`() {
        val state = AddUserUiState(
            name = "",
            email = "",
            phoneNumber = "",
            city = "",
            gender = null
        )
        val errors = AddUserFormValidator.validate(state)
        assertEquals(5, errors.size)
    }
}
