package thong.kotlin.pomodoro.features.pomodoro.music.domain

import androidx.compose.ui.graphics.vector.ImageVector

data class MusicTrack(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val audioUrl: String? = null
)
