package thong.kotlin.pomodoro.core.utils

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.horizontalScrollWithMouseWheel(state: LazyListState): Modifier = composed {
    val scope = rememberCoroutineScope()
    this.onPointerEvent(PointerEventType.Scroll) {
        val delta = it.changes.first().scrollDelta
        if (delta.y != 0f) {
            scope.launch {
                state.scrollBy(delta.y * 60f)
            }
        }
    }
}
