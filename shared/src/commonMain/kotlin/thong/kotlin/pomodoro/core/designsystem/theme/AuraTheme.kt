package thong.kotlin.pomodoro.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AuraColorScheme = darkColorScheme(
    background = AuraColors.Background,
    surface = AuraColors.Background,
    primary = AuraColors.WorkMode
)

@Composable
fun AuraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AuraColorScheme,
        typography = AuraTypography,
        shapes = AuraShapes,
        content = content
    )
}