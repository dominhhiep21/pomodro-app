package thong.kotlin.pomodoro.core.designsystem.components

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
actual fun AuraHorizontalScrollbar(
    state: LazyListState,
    modifier: Modifier
) {
    HorizontalScrollbar(
        adapter = rememberScrollbarAdapter(state),
        modifier = modifier
            .padding(horizontal = 8.dp)
            .padding(bottom = 2.dp)
            .clip(RoundedCornerShape(4.dp)),
        style = androidx.compose.foundation.ScrollbarStyle(
            minimalHeight = 16.dp,
            thickness = 4.dp,
            shape = RoundedCornerShape(4.dp),
            hoverDurationMillis = 300,
            unhoverColor = Color.White.copy(alpha = 0.12f),
            hoverColor = Color.Cyan.copy(alpha = 0.5f)
        )
    )
}
