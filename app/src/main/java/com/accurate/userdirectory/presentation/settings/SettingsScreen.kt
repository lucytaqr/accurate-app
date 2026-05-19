package com.accurate.userdirectory.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.accurate.userdirectory.core.designsystem.AccurateColors
import com.accurate.userdirectory.core.designsystem.components.AccurateButton
import com.accurate.userdirectory.core.designsystem.components.AccurateCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it.text)
            viewModel.onClearMessage()
        }
    }

    Scaffold(
        containerColor = AccurateColors.SurfaceSoft,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
                .statusBarsPadding()
        ) {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
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

            // Pink Accent Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(AccurateColors.PrimaryPink)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section: Informasi Aplikasi
                Text(
                    "Informasi Aplikasi",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AccurateColors.TextSecondary,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                AccurateCard {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = AccurateColors.PrimaryPink,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Accurate Directory",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                                Text(
                                    "Versi ${state.appVersion}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AccurateColors.TextSecondary
                                )
                            }
                        }
                        
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = AccurateColors.Divider
                        )
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "API Source",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AccurateColors.TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                state.apiSource,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = AccurateColors.TextSecondary
                            )
                        }
                    }
                }

                // Section: Sinkronisasi
                Text(
                    "Sinkronisasi",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AccurateColors.TextSecondary,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                AccurateCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val syncIcon = if (state.pendingSyncCount > 0) Icons.Default.SyncProblem else Icons.Default.CheckCircle
                            val syncColor = if (state.pendingSyncCount > 0) AccurateColors.Warning else AccurateColors.Success
                            
                            Icon(
                                syncIcon,
                                contentDescription = null,
                                tint = syncColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Status Data",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                                Text(
                                    if (state.pendingSyncCount > 0) "${state.pendingSyncCount} user belum tersinkron"
                                    else "Semua data sudah sinkron",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = syncColor
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = null,
                                tint = AccurateColors.TextTertiary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Update terakhir: ${state.lastSyncText}",
                                style = MaterialTheme.typography.bodySmall,
                                color = AccurateColors.TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AccurateButton(
                        text = if (state.isSyncing) "Menyinkronkan..." else "Refresh Data",
                        onClick = { viewModel.onRefreshData() },
                        modifier = Modifier.fillMaxWidth(),
                        isLoading = state.isSyncing
                    )

                    if (state.pendingSyncCount > 0) {
                        AccurateButton(
                            text = "Sinkronkan ${state.pendingSyncCount} Pending User",
                            onClick = { viewModel.onSyncPending() },
                            modifier = Modifier.fillMaxWidth(),
                            isLoading = state.isSyncing
                        )
                    }
                }
            }
        }
    }
}
