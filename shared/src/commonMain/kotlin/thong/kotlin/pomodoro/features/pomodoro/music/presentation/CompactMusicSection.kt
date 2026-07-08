package thong.kotlin.pomodoro.features.pomodoro.music.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.features.pomodoro.music.domain.MusicTrack

@Composable
fun CompactMusicSectionComponent(
    availableTracks: List<MusicTrack>,
    selectedTrackId: String?,
    isMusicPlaying: Boolean,
    onToggleMusic: () -> Unit,
    onSelectTrack: (String) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!compact) {
            Text(
                text = "Âm nhạc tập trung",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Music Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(availableTracks, key = { it.id }) { track ->
                val isSelected = track.id == selectedTrackId
                
                GlassBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSelectTrack(track.id) },
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = if (isSelected) AuraColors.WorkMode.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = track.icon,
                            contentDescription = null,
                            tint = if (isSelected) AuraColors.WorkMode else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = track.name,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.4f),
                            fontSize = 12.sp,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Clip,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.basicMarquee()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Play/Pause Button at Bottom
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(AuraColors.WorkMode.copy(alpha = 0.15f))
                .border(1.dp, AuraColors.WorkMode.copy(alpha = 0.3f), CircleShape)
                .clickable { onToggleMusic() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isMusicPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "Toggle Music",
                tint = AuraColors.WorkMode,
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = if (isMusicPlaying) "Đang phát" else "Đã tạm dừng",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        val activeTrack = availableTracks.find { it.id == selectedTrackId }
        activeTrack?.let {
            Text(
                text = it.name,
                color = Color.White,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .widthIn(max = 150.dp)
                    .basicMarquee()
            )
        }
    }
}