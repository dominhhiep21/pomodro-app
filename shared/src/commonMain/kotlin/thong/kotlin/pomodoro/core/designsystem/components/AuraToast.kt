package thong.kotlin.pomodoro.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thong.kotlin.pomodoro.core.notification.toast.AppToastHostState
import thong.kotlin.pomodoro.core.notification.toast.AppToastMessage
import thong.kotlin.pomodoro.core.notification.toast.AppToastType

@Composable
fun AuraToastHost(
    modifier: Modifier = Modifier,
    toastHostState: AppToastHostState
) {
    val toast by toastHostState.toast.collectAsState()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = toast != null,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it }
        ) {
            toast?.let {
                AuraToastItem(it)
            }
        }
    }
}

@Composable
private fun AuraToastItem(
    toast: AppToastMessage
) {
    val backgroundColor = when (toast.type) {
        AppToastType.SUCCESS -> Color(0xFF2E7D32).copy(alpha = 0.8f)
        AppToastType.ERROR -> Color(0xFFC62828).copy(alpha = 0.8f)
        AppToastType.WARNING -> Color(0xFFF9A825).copy(alpha = 0.8f)
        AppToastType.INFO -> Color(0xFF323232).copy(alpha = 0.8f)
    }

    val icon = when (toast.type) {
        AppToastType.SUCCESS -> "✅"
        AppToastType.ERROR -> "❌"
        AppToastType.WARNING -> "⚠️"
        AppToastType.INFO -> "ℹ️"
    }

    Row(
        modifier = Modifier
            .padding(bottom = 32.dp)
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = toast.message,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}