package thong.kotlin.pomodoro.features.pomodoro.ambient.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.components.AuraHorizontalScrollbar
import thong.kotlin.pomodoro.core.utils.horizontalScrollWithMouseWheel
import thong.kotlin.pomodoro.features.pomodoro.ambient.domain.AmbientSound

@Composable
fun AmbientSoundSection(
    availableSounds: List<AmbientSound>,
    activeSoundIds: Set<String>,
    onToggleSound: (String) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val lazyListState = rememberLazyListState()

    Column(modifier = modifier.fillMaxWidth()) {
        if (!compact) {
            Text(
                text = "Âm thanh môi trường",
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
                    items(availableSounds) { sound ->
                        val isActive = activeSoundIds.contains(sound.id)
                        
                        AmbientSoundItem(
                            sound = sound,
                            isActive = isActive,
                            compact = compact
                        ) { onToggleSound(sound.id) }
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
private fun AmbientSoundItem(
    sound: AmbientSound,
    isActive: Boolean,
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
                .background(if (isActive) Color.Cyan.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f))
                .border(
                    width = 2.dp,
                    color = if (isActive) Color.Cyan else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = sound.icon,
                contentDescription = sound.name,
                tint = if (isActive) Color.Cyan else Color.White,
                modifier = Modifier.size(if (compact) 24.dp else 28.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Text(
            text = sound.name,
            color = if (isActive) Color.Cyan else Color.White.copy(alpha = 0.7f),
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
