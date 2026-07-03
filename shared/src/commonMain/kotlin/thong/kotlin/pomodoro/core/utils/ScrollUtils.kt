package thong.kotlin.pomodoro.core.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.Modifier

expect fun Modifier.horizontalScrollWithMouseWheel(state: LazyListState): Modifier
