package com.accurate.userdirectory.presentation.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.accurate.userdirectory.core.designsystem.AccurateColors
import com.accurate.userdirectory.core.designsystem.components.EmptyStateView
import com.accurate.userdirectory.core.designsystem.components.LoadingSkeleton
import com.accurate.userdirectory.domain.model.ActivityLog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    viewModel: ActivityViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Activity", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = AccurateColors.Surface,
                titleContentColor = AccurateColors.TextPrimary
            )
        )

        Box(modifier = Modifier.fillMaxWidth().height(16.dp).background(AccurateColors.PrimaryPink))

        when {
            state.isLoading -> LoadingSkeleton()
            state.logs.isEmpty() -> EmptyStateView(
                title = "Belum Ada Aktivitas",
                subtitle = "Aktivitas seperti menambah user, refresh data, dan sync akan muncul di sini.",
                modifier = Modifier.fillMaxSize()
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
            ) {
                items(state.logs, key = { it.id }) { log ->
                    ActivityLogItem(log = log)
                }
            }
        }
    }
}

@Composable
fun ActivityLogItem(log: ActivityLog) {
    val timeText = rememberFormattedTime(log.createdAt)

    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = log.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = AccurateColors.TextPrimary
            )
            if (log.description.isNotBlank()) {
                Text(
                    text = log.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = AccurateColors.TextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        Text(
            text = timeText,
            style = MaterialTheme.typography.bodySmall,
            color = AccurateColors.TextTertiary
        )
    }
}

@Composable
fun rememberFormattedTime(timestamp: Long): String {
    val formatter = remember { SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID")) }
    return formatter.format(Date(timestamp))
}
