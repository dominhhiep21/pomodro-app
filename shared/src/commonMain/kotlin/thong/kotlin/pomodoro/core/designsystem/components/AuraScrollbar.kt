package thong.kotlin.pomodoro.core.designsystem.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A platform-aware scrollbar.
 * On Desktop, it will render a thin, elegant HorizontalScrollbar.
 * On other platforms, it will render nothing (empty placeholder).
 */
@Composable
expect fun AuraHorizontalScrollbar(
    state: LazyListState,
    modifier: Modifier = Modifier
)
