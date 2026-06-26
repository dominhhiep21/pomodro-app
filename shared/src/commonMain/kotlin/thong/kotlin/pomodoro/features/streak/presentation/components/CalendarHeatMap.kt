package thong.kotlin.pomodoro.features.streak.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.features.streak.domain.model.DailyRecord

@Composable
fun CalendarHeatMap(
    history: List<DailyRecord>,
    modifier: Modifier = Modifier,
    weeks: Int = 12
) {
    val totalDays = weeks * 7
    val sortedHistory = history.sortedByDescending { it.date }
    val recentDays = sortedHistory.take(totalDays).reversed()

    // Build grid: 7 rows (Mon-Sun) × N columns (weeks)
    val grid = Array(7) { row ->
        Array(weeks) { col ->
            val index = col * 7 + row
            if (index < recentDays.size) recentDays[index].sessionsCompleted else 0
        }
    }

    Column(modifier = modifier.padding(horizontal = 4.dp)) {
        for (row in 0 until 7) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                for (col in 0 until weeks) {
                    val sessions = grid[row][col]
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(3.dp))
                            .background(intensityColor(sessions))
                    )
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
        }
    }
}

private fun intensityColor(sessions: Int): Color = when {
    sessions == 0 -> AuraColors.surface.copy(alpha = 0.15f)
    sessions == 1 -> AuraColors.primary.copy(alpha = 0.2f)
    sessions in 2..3 -> AuraColors.primary.copy(alpha = 0.5f)
    sessions in 4..5 -> AuraColors.primary.copy(alpha = 0.8f)
    else -> AuraColors.primary
}
