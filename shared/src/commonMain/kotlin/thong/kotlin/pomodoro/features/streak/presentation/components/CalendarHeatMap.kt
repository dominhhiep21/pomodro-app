package thong.kotlin.pomodoro.features.streak.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.features.streak.domain.model.DailyRecord

private val DAY_LABELS = listOf("M", "T", "W", "T", "F", "S", "S")

@Composable
fun CalendarHeatMap(
    history: List<DailyRecord>,
    accentColor: Color = AuraColors.WorkMode,
    modifier: Modifier = Modifier,
    weeks: Int = 14,
    cellSize: Dp = 14.dp,
    cellSpacing: Dp = 3.dp
) {
    val totalDays = weeks * 7
    val sortedHistory = history.sortedBy { it.date }
    // Pad to full grid — most recent day at bottom-right
    val padded = buildList {
        repeat(totalDays - sortedHistory.size) { add(null) }
        addAll(sortedHistory.takeLast(totalDays))
    }

    // grid[col][row]: col 0 = oldest week, row 0 = Monday
    val grid: List<List<DailyRecord?>> = List(weeks) { col ->
        List(7) { row -> padded.getOrNull(col * 7 + row) }
    }

    // Collect unique "Month Year" labels per week for month header
    val monthLabels: List<String?> = grid.map { col ->
        col.filterNotNull().firstOrNull()?.let { extractMonthLabel(it.date) }
    }

    Column(modifier = modifier) {
        // Month labels row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp),   // align with grid (skip day-label column)
            horizontalArrangement = Arrangement.spacedBy(cellSpacing)
        ) {
            var lastLabel: String? = null
            grid.indices.forEach { col ->
                val label = monthLabels[col]
                val showLabel = label != null && label != lastLabel
                if (showLabel) lastLabel = label
                Box(modifier = Modifier.weight(1f)) {
                    if (showLabel) {
                        Text(
                            text = label!!,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = AuraColors.TextSecondary,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Day labels + grid
        Row(modifier = Modifier.fillMaxWidth()) {
            // Day-of-week labels column
            Column(
                modifier = Modifier.width(16.dp),
                verticalArrangement = Arrangement.spacedBy(cellSpacing)
            ) {
                DAY_LABELS.forEachIndexed { idx, label ->
                    // Only show Mon, Wed, Fri labels (indices 0, 2, 4) to avoid clutter
                    val showDayLabel = idx % 2 == 0
                    Box(
                        modifier = Modifier
                            .height(cellSize)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        if (showDayLabel) {
                            Text(
                                text = label,
                                fontSize = 8.sp,
                                color = AuraColors.TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Heat map grid
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(cellSpacing)
            ) {
                grid.forEachIndexed { colIdx, col ->
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(cellSpacing)
                    ) {
                        col.forEachIndexed { rowIdx, record ->
                            HeatCell(
                                sessions = record?.sessionsCompleted ?: 0,
                                accentColor = accentColor,
                                cellSize = cellSize,
                                // Staggered fade-in from left to right, top to bottom
                                delayMs = (colIdx * 7 + rowIdx) * 8
                            )
                        }
                    }
                }
            }
        }

        // Legend
        Spacer(modifier = Modifier.height(8.dp))
        HeatMapLegend(accentColor = accentColor)
    }
}

@Composable
private fun HeatCell(
    sessions: Int,
    accentColor: Color,
    cellSize: Dp,
    delayMs: Int
) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 300, delayMillis = delayMs),
        label = "CellFade"
    )
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .size(cellSize)
            .alpha(alpha)
            .clip(RoundedCornerShape(3.dp))
            .background(intensityColor(sessions, accentColor))
    )
}

@Composable
private fun HeatMapLegend(accentColor: Color) {
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Less",
            fontSize = 9.sp,
            color = AuraColors.TextSecondary
        )
        Spacer(modifier = Modifier.width(4.dp))
        listOf(0, 1, 2, 3, 4).forEach { level ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 1.dp)
                    .size(10.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(legendColor(level, accentColor))
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "More",
            fontSize = 9.sp,
            color = AuraColors.TextSecondary
        )
    }
}

private fun intensityColor(sessions: Int, accentColor: Color): Color = when {
    sessions == 0 -> Color.White.copy(alpha = 0.06f)
    sessions == 1 -> accentColor.copy(alpha = 0.22f)
    sessions in 2..3 -> accentColor.copy(alpha = 0.45f)
    sessions in 4..5 -> accentColor.copy(alpha = 0.72f)
    else -> accentColor.copy(alpha = 0.95f)
}

private fun legendColor(level: Int, accentColor: Color): Color = when (level) {
    0 -> Color.White.copy(alpha = 0.06f)
    1 -> accentColor.copy(alpha = 0.22f)
    2 -> accentColor.copy(alpha = 0.45f)
    3 -> accentColor.copy(alpha = 0.72f)
    else -> accentColor.copy(alpha = 0.95f)
}

// Extract abbreviated "Jan", "Feb" etc. from "YYYY-MM-DD" date string
private fun extractMonthLabel(date: String): String? {
    val month = date.split("-").getOrNull(1)?.toIntOrNull() ?: return null
    return listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
        .getOrNull(month - 1)
}
