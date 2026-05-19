package com.accurate.userdirectory.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.accurate.userdirectory.R
import com.accurate.userdirectory.core.designsystem.AccurateColors
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToUserList: () -> Unit,
) {
    var isReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(1500)
        isReady = true
    }

    LaunchedEffect(isReady) {
        if (isReady) onNavigateToUserList()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AccurateColors.Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.image_splash_screen),
                contentDescription = "Accurate Logo",
                tint = Color.Unspecified,
                modifier = Modifier.size(420.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Memudahkan pengelolaan bisnis Anda",
                style = MaterialTheme.typography.bodyMedium,
                color = AccurateColors.TextTertiary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(32.dp))
            CircularProgressIndicator(
                color = AccurateColors.PrimaryPink,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
