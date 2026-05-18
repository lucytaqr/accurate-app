package com.accurate.userdirectory.core.designsystem.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.accurate.userdirectory.core.designsystem.AccurateColors

@Composable
fun AccurateButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AccurateColors.PrimaryPink,
            contentColor = AccurateColors.Surface,
            disabledContainerColor = AccurateColors.PrimaryPinkLight.copy(alpha = 0.5f),
            disabledContentColor = AccurateColors.Surface.copy(alpha = 0.7f)
        ),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        Text(
            text = if (isLoading) "Memproses..." else text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}
