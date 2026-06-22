package thong.kotlin.pomodoro.features._redefine

import androidx.lifecycle.ViewModel

// Chứa toàn bộ trạng thái về Giao diện và Âm thanh
data class WorkspaceUiState(
    // Background
    val selectedBackgroundId: String = "default_bg_id",
    val availableBackgrounds: List<Any> = emptyList(),
    val backgroundConfig: Any? = null,

    // Music
    val isMusicPlaying: Boolean = false,
    val selectedTrackId: String? = null,
    val availableTracks: List<Any> = emptyList(),
    val musicPosition: Long = 0L,

    // Ambient Sounds
    val activeAmbientSoundIds: Set<String> = emptySet(),
    val availableAmbientSounds: List<Any> = emptyList(),

    // UI Modes & Toggles
    val isCompactMode: Boolean = false,
    val isCompactMenuExpanded: Boolean = false,
    val activeCompactSection: Any? = null,
    val isNotificationEnabled: Boolean = false,

    // Settings State (Trạng thái của form cài đặt)
    val isSettingsVisible: Boolean = false,
    val editingWorkMinutes: String = "25",
    val editingBreakMinutes: String = "5",
    val settingsError: String? = null
)

class WorkspaceViewModel() : ViewModel() {
}