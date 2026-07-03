package thong.kotlin.pomodoro.core.designsystem.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun AuraHorizontalScrollbar(
    state: LazyListState,
    modifier: Modifier
) {
    // iOS doesn't typically use visual scrollbars for LazyRows
}
