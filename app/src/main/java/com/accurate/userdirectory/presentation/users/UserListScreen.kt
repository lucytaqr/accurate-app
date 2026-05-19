package com.accurate.userdirectory.presentation.users

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import com.accurate.userdirectory.worker.UserSyncWorker
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.accurate.userdirectory.core.designsystem.AccurateColors
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
    onNavigateToEditUser: (String) -> Unit = {},
    viewModel: UserListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            kotlinx.coroutines.delay(3000)
            viewModel.onClearError()
        }
    }

    LaunchedEffect(state.actionMessage) {
        state.actionMessage?.let { msg ->
            if (msg.text.startsWith("edit:")) {
                val userId = msg.text.removePrefix("edit:")
                onNavigateToEditUser(userId)
                viewModel.onClearActionMessage()
            } else {
                kotlinx.coroutines.delay(3000)
                viewModel.onClearActionMessage()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(AccurateColors.Surface).statusBarsPadding()) {

        // Offline Banner
        if (state.isOffline) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AccurateColors.OfflineBannerBg)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
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
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = AccurateColors.Surface,
                titleContentColor = AccurateColors.TextPrimary
            ),
            windowInsets = WindowInsets(0.dp)
        )

        Box(modifier = Modifier.fillMaxWidth().height(16.dp).background(AccurateColors.PrimaryPink))


        // Search and Filter Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.filter.keyword,
                onValueChange = viewModel::onSearchChanged,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Cari nama, email, atau kota...", style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = AccurateColors.TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccurateColors.Border,
                    unfocusedBorderColor = AccurateColors.Border,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = AccurateColors.PrimaryPink
                )
            )

            Button(
                onClick = { viewModel.onFilterClicked() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccurateColors.PrimaryPink,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Icon(
                    Icons.Default.FilterList,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Filter", color = Color.White)
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChipView(
                    label = "Semua Kota",
                    isSelected = state.filter.selectedCities.isEmpty(),
                    onClick = { viewModel.onResetFilter() }
                )
            }
            items(state.cities) { city ->
                val isSelected = city.name in state.filter.selectedCities
                FilterChipView(
                    label = city.name,
                    isSelected = isSelected,
                    onClick = { 
                        if (isSelected) viewModel.onRemoveCityFilter(city.name)
                        else {
                            val newCities = state.filter.selectedCities + city.name
                            viewModel.onTempFilterChanged(state.filter.copy(selectedCities = newCities))
                            viewModel.onApplyFilter()
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sort Dropdown
        var showSortMenu by remember { mutableStateOf(false) }
        Box(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
            Row(
                modifier = Modifier
                    .clickable { showSortMenu = true }
                    .background(AccurateColors.Surface, RoundedCornerShape(8.dp))
                    .border(1.dp, AccurateColors.Border, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Urutkan: ${state.filter.sortOption.displayName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AccurateColors.TextPrimary
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
                        text = { Text(option.displayName) },
                        onClick = {
                            viewModel.onSortChanged(option)
                            showSortMenu = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Summary Card
        if (!state.isEmpty) {
            val context = LocalContext.current
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccurateColors.SummaryBg)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Menampilkan ${state.displayedUserCount} user",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = AccurateColors.TextPrimary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        "Terakhir diperbarui: ${state.lastUpdatedText.replace("Last update: ", "")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AccurateColors.TextSecondary
                    )
                }

                if (state.pendingSyncCount > 0) {
                    IconButton(
                        onClick = { UserSyncWorker.enqueue(context) },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .border(1.dp, AccurateColors.Border, RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Sync",
                            tint = AccurateColors.PrimaryPink,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Content
        val contentModifier = Modifier.weight(1f)
        when {
            state.isInitialLoading -> Box(modifier = contentModifier) { LoadingSkeleton() }
            state.errorMessage != null && state.users.isEmpty() -> Box(modifier = contentModifier) {
                ErrorStateView(
                    message = state.errorMessage!!.text,
                    onRetry = { viewModel.onRetry() }
                )
            }
            state.isEmptyFilterResult -> Box(modifier = contentModifier) {
                EmptyStateView(
                    title = "Tidak Ada Hasil",
                    subtitle = "Tidak ada user yang sesuai dengan filter atau pencarian Anda.",
                    actionLabel = "Reset Filter",
                    onAction = { viewModel.onResetFilter() }
                )
            }
            state.isEmpty -> Box(modifier = contentModifier) {
                EmptyStateView(
                    title = "Belum Ada User",
                    subtitle = "Tambahkan user pertama Anda sekarang.",
                    actionLabel = "Tambah User",
                    onAction = onNavigateToAddUser
                )
            }
            else -> LazyColumn(
                modifier = contentModifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.displayedUsers, key = { it.id }) { user ->
                    UserCard(
                        name = user.name,
                        email = user.email,
                        city = user.city,
                        gender = user.gender,
                        photoUri = user.photoUri,
                        syncStatus = user.syncStatus,
                        onEditClick = { viewModel.onEditUser(user.id) },
                        onDeleteClick = { viewModel.onShowDeleteDialog(user) }
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
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

    // Delete Confirmation Dialog
    if (state.showDeleteDialog && state.deleteTargetUser != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissDeleteDialog() },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah Anda yakin ingin menghapus user \"${state.deleteTargetUser!!.name}\"?") },
            confirmButton = {
                TextButton(onClick = { viewModel.onConfirmDelete() }) {
                    Text("Hapus", color = AccurateColors.Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissDeleteDialog() }) {
                    Text("Batal")
                }
            }
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
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Color.White,
        tonalElevation = 0.dp,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    "Filter",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AccurateColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Terapkan filter untuk menemukan user dengan mudah",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AccurateColors.TextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Kota",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AccurateColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = citySearchQuery,
                    onValueChange = { citySearchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Cari kota...", color = AccurateColors.TextTertiary)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccurateColors.Border,
                        unfocusedBorderColor = AccurateColors.Border,
                        focusedContainerColor = AccurateColors.Surface,
                        unfocusedContainerColor = AccurateColors.Surface
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // City List
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Option: Semua Kota
                    val isAllCitiesSelected = tempFilter.selectedCities.isEmpty()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTempFilterChanged(tempFilter.copy(selectedCities = emptySet())) }
                            .padding(vertical = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .border(
                                    width = 1.dp,
                                    color = if (isAllCitiesSelected) AccurateColors.PrimaryPink else AccurateColors.Border,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .background(
                                    if (isAllCitiesSelected) AccurateColors.PrimaryPink else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isAllCitiesSelected) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Semua Kota",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AccurateColors.TextPrimary
                        )
                    }

                    filteredCities.forEach { city ->
                        val isSelected = city.name in tempFilter.selectedCities
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val newCities = if (isSelected) tempFilter.selectedCities - city.name
                                    else tempFilter.selectedCities + city.name
                                    onTempFilterChanged(tempFilter.copy(selectedCities = newCities))
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) AccurateColors.PrimaryPink else AccurateColors.Border,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .background(
                                        if (isSelected) AccurateColors.PrimaryPink else Color.Transparent,
                                        RoundedCornerShape(6.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                city.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = AccurateColors.TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Jenis Kelamin",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AccurateColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                listOf(null, Gender.Male, Gender.Female).forEach { gender ->
                    val isSelected = tempFilter.selectedGender == gender
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTempFilterChanged(tempFilter.copy(selectedGender = gender)) }
                            .padding(vertical = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) AccurateColors.PrimaryPink else AccurateColors.Border,
                                    shape = CircleShape
                                )
                                .background(
                                    if (isSelected) AccurateColors.PrimaryPink else Color.Transparent,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            when (gender) {
                                null -> "Semua"
                                Gender.Male -> "Male"
                                Gender.Female -> "Female"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = AccurateColors.TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Bottom Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onReset,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AccurateColors.PrimaryPink),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = AccurateColors.PrimaryPink
                    )
                ) {
                    Text("Reset", fontWeight = FontWeight.Bold, color = AccurateColors.PrimaryPink)
                }

                Button(
                    onClick = onApply,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccurateColors.PrimaryPink)
                ) {
                    Text("Terapkan Filter", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
