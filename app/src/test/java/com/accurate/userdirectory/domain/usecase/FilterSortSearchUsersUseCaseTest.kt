package com.accurate.userdirectory.domain.usecase

import com.accurate.userdirectory.domain.model.Gender
import com.accurate.userdirectory.domain.model.SortOption
import com.accurate.userdirectory.domain.model.SyncStatus
import com.accurate.userdirectory.domain.model.User
import com.accurate.userdirectory.domain.model.UserFilter
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FilterSortSearchUsersUseCaseTest {

    private lateinit var useCase: FilterSortSearchUsersUseCase

    private val users = listOf(
        User("1", "1", "Budi", "budi@email.com", "081", "Jl A", "Jakarta", Gender.Male, null, SyncStatus.Synced, 0, 0),
        User("2", "2", "Ani", "ani@email.com", "082", "Jl B", "Bandung", Gender.Female, null, SyncStatus.Synced, 0, 0),
        User("3", "3", "Citra", "citra@email.com", "083", "Jl C", "Jakarta", Gender.Female, null, SyncStatus.Synced, 0, 0),
        User("4", "4", "Dedi", "dedi@email.com", "084", "Jl D", "Surabaya", Gender.Male, null, SyncStatus.Synced, 0, 0)
    )

    @Before
    fun setUp() {
        useCase = FilterSortSearchUsersUseCase()
    }

    @Test
    fun `empty filter returns all users`() {
        val result = useCase(users, UserFilter())
        assertEquals(4, result.size)
    }

    @Test
    fun `search by name returns matching users`() {
        val filter = UserFilter(keyword = "Budi")
        val result = useCase(users, filter)
        assertEquals(1, result.size)
        assertEquals("Budi", result[0].name)
    }

    @Test
    fun `search by email returns matching users`() {
        val filter = UserFilter(keyword = "ani@email")
        val result = useCase(users, filter)
        assertEquals(1, result.size)
        assertEquals("Ani", result[0].name)
    }

    @Test
    fun `search by city returns matching users`() {
        val filter = UserFilter(keyword = "Jakarta")
        val result = useCase(users, filter)
        assertEquals(2, result.size)
    }

    @Test
    fun `filter by city returns correct users`() {
        val filter = UserFilter(selectedCities = setOf("Jakarta"))
        val result = useCase(users, filter)
        assertEquals(2, result.size)
        result.forEach { assertEquals("Jakarta", it.city) }
    }

    @Test
    fun `filter by multiple cities`() {
        val filter = UserFilter(selectedCities = setOf("Jakarta", "Bandung"))
        val result = useCase(users, filter)
        assertEquals(3, result.size)
    }

    @Test
    fun `filter by gender male`() {
        val filter = UserFilter(selectedGender = Gender.Male)
        val result = useCase(users, filter)
        assertEquals(2, result.size)
    }

    @Test
    fun `filter by gender female`() {
        val filter = UserFilter(selectedGender = Gender.Female)
        val result = useCase(users, filter)
        assertEquals(2, result.size)
    }

    @Test
    fun `sort ascending by name`() {
        val filter = UserFilter(sortOption = SortOption.NameAsc)
        val result = useCase(users, filter)
        assertEquals("Ani", result[0].name)
        assertEquals("Budi", result[1].name)
        assertEquals("Citra", result[2].name)
        assertEquals("Dedi", result[3].name)
    }

    @Test
    fun `sort descending by name`() {
        val filter = UserFilter(sortOption = SortOption.NameDesc)
        val result = useCase(users, filter)
        assertEquals("Dedi", result[0].name)
        assertEquals("Citra", result[1].name)
        assertEquals("Budi", result[2].name)
        assertEquals("Ani", result[3].name)
    }

    @Test
    fun `combined search filter and sort`() {
        val filter = UserFilter(
            keyword = "a",
            selectedCities = setOf("Jakarta"),
            selectedGender = Gender.Female,
            sortOption = SortOption.NameAsc
        )
        val result = useCase(users, filter)
        assertEquals(1, result.size)
        assertEquals("Citra", result[0].name)
    }

    @Test
    fun `case insensitive search`() {
        val filter = UserFilter(keyword = "budi")
        val result = useCase(users, filter)
        assertEquals(1, result.size)
        assertEquals("Budi", result[0].name)
    }

    @Test
    fun `empty search result`() {
        val filter = UserFilter(keyword = "XYZ123")
        val result = useCase(users, filter)
        assertEquals(0, result.size)
    }
}
