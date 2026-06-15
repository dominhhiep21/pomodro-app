package thong.kotlin.pomodoro.features.pomodoro.timer.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.components.AuraHorizontalScrollbar
import thong.kotlin.pomodoro.core.utils.horizontalScrollWithMouseWheel
import thong.kotlin.pomodoro.features.settings.domain.AppBackground

@Composable
fun BackgroundSection(
    availableBackgrounds: List<AppBackground>,
    selectedBackgroundId: String?,
    onSelectBackground: (String) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val lazyListState = rememberLazyListState()

    Column(modifier = modifier.fillMaxWidth()) {
        if (!compact) {
            Text(
                text = "Hình nền ứng dụng",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        GlassBox(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(if (compact) 16.dp else 20.dp),
            backgroundColor = Color.White.copy(alpha = 0.05f)
        ) {
            Column {
                LazyRow(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScrollWithMouseWheel(lazyListState)
                        .padding(top = if (compact) 8.dp else 12.dp)
                        .padding(horizontal = if (compact) 8.dp else 12.dp)
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(availableBackgrounds) { background ->
                        val isSelected = background.id == selectedBackgroundId
                        
                        BackgroundItem(
                            background = background,
                            isSelected = isSelected,
                            compact = compact
                        ) { onSelectBackground(background.id) }
                    }
                }

                AuraHorizontalScrollbar(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun BackgroundItem(
    background: AppBackground,
    isSelected: Boolean,
    compact: Boolean = false,
    onClick: () -> Unit
) {
    val size = if (compact) 50.dp else 60.dp
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(if (compact) 70.dp else 80.dp)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 2.dp,
                    color = if (isSelected) Color.Cyan else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { onClick() }
        ) {
            Image(
                painter = painterResource(background.resource),
                contentDescription = background.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Cyan.copy(alpha = 0.2f))
                )
            }
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Text(
            text = background.name,
            color = if (isSelected) Color.Cyan else Color.White.copy(alpha = 0.7f),
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
