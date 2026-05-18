package com.accurate.userdirectory.presentation.users

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.accurate.userdirectory.core.designsystem.AccurateColors
import com.accurate.userdirectory.core.designsystem.components.AccurateButton
import com.accurate.userdirectory.core.designsystem.components.EmptyStateView
import com.accurate.userdirectory.core.designsystem.components.ErrorStateView
import com.accurate.userdirectory.core.designsystem.components.FilterChipView
import com.accurate.userdirectory.core.designsystem.components.LoadingSkeleton
import com.accurate.userdirectory.core.designsystem.components.UserCard
import com.accurate.userdirectory.domain.model.City
import com.accurate.userdirectory.domain.model.Gender
import com.accurate.userdirectory.domain.model.SortOption
import com.accurate.userdirectory.domain.model.UserFilter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun UserListScreen(
    onNavigateToAddUser: () -> Unit,
    viewModel: UserListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            kotlinx.coroutines.delay(3000)
            viewModel.onClearError()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Offline Banner
        if (state.isOffline) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AccurateColors.OfflineBannerBg)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.WifiOff,
                    contentDescription = null,
                    tint = AccurateColors.OfflineBannerText,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Anda sedang offline. Data yang ditampilkan adalah data cache.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AccurateColors.OfflineBannerText
                )
            }
        }

        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    "User Directory",
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                IconButton(onClick = { viewModel.onRefresh() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = AccurateColors.Surface,
                titleContentColor = AccurateColors.TextPrimary
            )
        )

        // Search Bar
        OutlinedTextField(
            value = state.filter.keyword,
            onValueChange = viewModel::onSearchChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Cari nama, email, atau kota...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = AccurateColors.TextTertiary)
            },
            trailingIcon = {
                if (state.filter.keyword.isNotBlank()) {
                    IconButton(onClick = { viewModel.onSearchChanged("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccurateColors.PrimaryPink,
                unfocusedBorderColor = AccurateColors.Border,
                cursorColor = AccurateColors.PrimaryPink
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter and Sort Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Filter Button
            TextButton(
                onClick = { viewModel.onFilterClicked() },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.FilterList,
                    contentDescription = null,
                    tint = if (state.filter.isActive) AccurateColors.PrimaryPink else AccurateColors.TextSecondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Filter",
                    color = if (state.filter.isActive) AccurateColors.PrimaryPink else AccurateColors.TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Sort Dropdown
            var showSortMenu by remember { mutableStateOf(false) }
            Box {
                TextButton(
                    onClick = { showSortMenu = true },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Urutkan: ${state.filter.sortOption.displayName}",
                        color = AccurateColors.TextSecondary
                    )
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = AccurateColors.TextSecondary
                    )
                }
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false },
                    offset = DpOffset(0.dp, 4.dp)
                ) {
                    SortOption.entries.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(option.displayName)
                                    if (state.filter.sortOption == option) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = AccurateColors.PrimaryPink,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            onClick = {
                                viewModel.onSortChanged(option)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Active Filter Chips
        if (state.filter.isActive) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.filter.selectedCities.forEach { city ->
                    FilterChipView(
                        label = city,
                        isSelected = true,
                        onClick = { viewModel.onRemoveCityFilter(city) }
                    )
                }
                if (state.filter.selectedGender != null) {
                    FilterChipView(
                        label = state.filter.selectedGender!!.displayName,
                        isSelected = true,
                        onClick = { viewModel.onRemoveGenderFilter() }
                    )
                }
                TextButton(onClick = { viewModel.onResetFilter() }) {
                    Text("Reset", color = AccurateColors.Error, style = MaterialTheme.typography.labelMedium)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Summary Card
        if (!state.isEmpty) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Menampilkan ${state.displayedUserCount} dari ${state.totalUserCount} user",
                    style = MaterialTheme.typography.bodySmall,
                    color = AccurateColors.TextTertiary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    state.lastUpdatedText,
                    style = MaterialTheme.typography.bodySmall,
                    color = AccurateColors.TextTertiary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Content
        when {
            state.isInitialLoading -> LoadingSkeleton()
            state.errorMessage != null && state.users.isEmpty() -> ErrorStateView(
                message = state.errorMessage!!.text,
                onRetry = { viewModel.onRetry() }
            )
            state.isEmptyFilterResult -> EmptyStateView(
                title = "Tidak Ada Hasil",
                subtitle = "Tidak ada user yang sesuai dengan filter atau pencarian Anda.",
                actionLabel = "Reset Filter",
                onAction = { viewModel.onResetFilter() }
            )
            state.isEmpty -> EmptyStateView(
                title = "Belum Ada User",
                subtitle = "Tambahkan user pertama Anda sekarang.",
                actionLabel = "Tambah User",
                onAction = onNavigateToAddUser
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.displayedUsers, key = { it.id }) { user ->
                    UserCard(
                        name = user.name,
                        email = user.email,
                        city = user.city,
                        gender = user.gender,
                        photoUri = user.photoUri,
                        syncStatus = user.syncStatus
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    // Filter Bottom Sheet
    if (state.showFilterSheet) {
        FilterBottomSheet(
            tempFilter = state.tempFilter,
            cities = state.cities,
            onTempFilterChanged = { viewModel.onTempFilterChanged(it) },
            onDismiss = { viewModel.onFilterDismissed() },
            onApply = { viewModel.onApplyFilter() },
            onReset = { viewModel.onResetFilter() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    tempFilter: UserFilter,
    cities: List<City>,
    onTempFilterChanged: (UserFilter) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var citySearchQuery by remember { mutableStateOf("") }

    val filteredCities = remember(citySearchQuery, cities) {
        if (citySearchQuery.isBlank()) cities
        else cities.filter { it.name.contains(citySearchQuery, ignoreCase = true) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = AccurateColors.Surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text("Filter", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            // City Search
            OutlinedTextField(
                value = citySearchQuery,
                onValueChange = { citySearchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cari kota...") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccurateColors.PrimaryPink,
                    unfocusedBorderColor = AccurateColors.Border
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // City Section
            Text("Kota", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            // All Cities checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onTempFilterChanged(tempFilter.copy(selectedCities = emptySet()))
                    }
                    .padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = tempFilter.selectedCities.isEmpty(),
                    onCheckedChange = {
                        onTempFilterChanged(tempFilter.copy(selectedCities = emptySet()))
                    },
                    colors = CheckboxDefaults.colors(checkedColor = AccurateColors.PrimaryPink)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Semua Kota", style = MaterialTheme.typography.bodyLarge)
            }

            filteredCities.forEach { city ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val newCities = if (city.name in tempFilter.selectedCities) {
                                tempFilter.selectedCities - city.name
                            } else {
                                tempFilter.selectedCities + city.name
                            }
                            onTempFilterChanged(tempFilter.copy(selectedCities = newCities))
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = city.name in tempFilter.selectedCities,
                        onCheckedChange = {
                            val newCities = if (it) tempFilter.selectedCities + city.name
                            else tempFilter.selectedCities - city.name
                            onTempFilterChanged(tempFilter.copy(selectedCities = newCities))
                        },
                        colors = CheckboxDefaults.colors(checkedColor = AccurateColors.PrimaryPink)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(city.name, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gender Section
            Text("Jenis Kelamin", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            listOf(null, Gender.Male, Gender.Female).forEach { gender ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onTempFilterChanged(tempFilter.copy(selectedGender = gender))
                        }
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = tempFilter.selectedGender == gender,
                        onClick = { onTempFilterChanged(tempFilter.copy(selectedGender = gender)) },
                        colors = RadioButtonDefaults.colors(selectedColor = AccurateColors.PrimaryPink)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        when (gender) {
                            null -> "Semua"
                            Gender.Male -> "Male"
                            Gender.Female -> "Female"
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = onReset,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Reset", color = AccurateColors.TextSecondary)
                }
                AccurateButton(
                    text = "Terapkan Filter",
                    onClick = onApply,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
