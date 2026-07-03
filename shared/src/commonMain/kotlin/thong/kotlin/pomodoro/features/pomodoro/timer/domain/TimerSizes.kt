package thong.kotlin.pomodoro.features.pomodoro.timer.domain

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

data class TimerSizes(
    val outerSize: Dp,
    val progressSize: Dp,
    val glassSize: Dp,
    val cornerRadius: Dp,
    val textSize: TextUnit
)