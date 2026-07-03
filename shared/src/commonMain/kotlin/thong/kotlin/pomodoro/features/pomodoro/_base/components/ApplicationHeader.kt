package thong.kotlin.pomodoro.features.pomodoro._base.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ZoomInMap
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import thong.kotlin.pomodoro.core.designsystem.components.AuraHeader

@Composable
fun ApplicationHeaderComponent(
    onToggleCompactMode: () -> Unit,
    onToggleSettings: () -> Unit,
    onExit: () -> Unit
) {

    AuraHeader(
        title = "Aura Pomo",
        subtitle = "Tìm kiếm dòng chảy học tập",
        actionButton = {
            Row {
                IconButton(onClick = onToggleCompactMode) {
                    Icon(
                        imageVector = Icons.Default.ZoomInMap,
                        contentDescription = "Compact Mode",
                        tint = Color.White.copy(alpha = 0.6f)
                    )
                }
                IconButton(onClick = onToggleSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White.copy(alpha = 0.6f)
                    )
                }
                IconButton(onClick = onExit) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Exit to Style Selection",
                        tint = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    )
}