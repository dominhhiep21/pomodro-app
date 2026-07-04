package thong.kotlin.pomodoro.features.pomodoro.ambient.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.features.pomodoro.ambient.domain.AmbientSound

@Composable
fun CompactAmbientSoundSection(
    availableSounds: List<AmbientSound>,
    activeSoundIds: Set<String>,
    onToggleSound: (String) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val isAnyPlaying = activeSoundIds.isNotEmpty()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (!compact) {
            Text(
                text = "Âm thanh môi trường",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Ambient Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(availableSounds, key = { it.id }) { sound ->
                val isActive = activeSoundIds.contains(sound.id)
                
                GlassBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onToggleSound(sound.id) },
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = if (isActive) Color.Cyan.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = sound.icon,
                            contentDescription = null,
                            tint = if (isActive) Color.Cyan else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = sound.name,
                            color = if (isActive) Color.White else Color.White.copy(alpha = 0.4f),
                            fontSize = 12.sp,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Master Control at Bottom
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (isAnyPlaying) Color.Cyan.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f))
                .border(1.dp, if (isAnyPlaying) Color.Cyan.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f), CircleShape)
                .clickable(enabled = isAnyPlaying) { 
                    // Stop all logic
                    activeSoundIds.toList().forEach { onToggleSound(it) }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isAnyPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "Toggle Ambient",
                tint = if (isAnyPlaying) Color.Cyan else Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = if (isAnyPlaying) "Đang phát ${activeSoundIds.size} âm thanh" else "Đã tạm dừng",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
