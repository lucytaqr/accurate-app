package com.accurate.userdirectory.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
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
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AccurateColors.Surface,
                    titleContentColor = AccurateColors.TextPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // App Info Card
            AccurateCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = AccurateColors.PrimaryPink)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Accurate Directory", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text("Versi ${state.appVersion}", style = MaterialTheme.typography.bodySmall, color = AccurateColors.TextSecondary)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = AccurateColors.Divider)
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text("API Source: ", style = MaterialTheme.typography.bodySmall, color = AccurateColors.TextSecondary)
                    Text(state.apiSource, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
            }

            // Sync Info Card
            AccurateCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (state.pendingSyncCount > 0) Icons.Default.SyncProblem else Icons.Default.CloudSync,
                        contentDescription = null,
                        tint = if (state.pendingSyncCount > 0) AccurateColors.Warning else AccurateColors.Success
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Status Sinkronisasi", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text(state.lastSyncText, style = MaterialTheme.typography.bodySmall, color = AccurateColors.TextSecondary)
                        Text(
                            if (state.pendingSyncCount > 0) "${state.pendingSyncCount} user belum tersinkron"
                            else "Semua user tersinkron",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.pendingSyncCount > 0) AccurateColors.Warning else AccurateColors.Success
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Buttons
            AccurateCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
