package thong.kotlin.pomodoro.features.pomodoro.viewmodel

import thong.kotlin.pomodoro.features.background.model.BackgroundConfig
import thong.kotlin.pomodoro.features.learning.mode.domain.LearningGroupConfig
import thong.kotlin.pomodoro.features.learning.mode.domain.LearningStyle
import thong.kotlin.pomodoro.features.pomodoro._base.domain.CompactSection
import thong.kotlin.pomodoro.features.pomodoro._base.domain.PomodoroMode
import thong.kotlin.pomodoro.features.pomodoro.ambient.data.AmbientSoundRepository
import thong.kotlin.pomodoro.features.pomodoro.ambient.domain.AmbientSound
import thong.kotlin.pomodoro.features.pomodoro.music.data.MusicRepository
import thong.kotlin.pomodoro.features.pomodoro.music.domain.MusicTrack
import thong.kotlin.pomodoro.features.session.domain.LearningSessionRecord
import thong.kotlin.pomodoro.features.background.data.BackgroundRepository
import thong.kotlin.pomodoro.features.background.model.AppBackground
import thong.kotlin.pomodoro.features.focus.score.domain.FocusScoreResult
import thong.kotlin.pomodoro.features.focus.tree.domain.FocusTreeGrowthResult
import thong.kotlin.pomodoro.features.focus.tree.domain.FocusTreeRecord
import thong.kotlin.pomodoro.features.focus.tree.presentation.animation.FocusTreeAnimationEvent

data class WorkspaceUiState(
    val currentMode: PomodoroMode = PomodoroMode.WORK,
    val learningStyle: LearningStyle = LearningStyle.SOLO,
    val learningGroupConfig: LearningGroupConfig? = null,
    val currentSession: LearningSessionRecord,
    val isChatExpanded: Boolean = false,

    // Background
    val selectedBackgroundId: String? = BackgroundRepository.DEFAULT_BACKGROUND_ID,
    val availableBackgrounds: List<AppBackground> = BackgroundRepository.availableBackgrounds,
    val backgroundConfig: BackgroundConfig? = null,

    // Music
    val isMusicPlaying: Boolean = false,
    val selectedTrackId: String? = MusicRepository.DEFAULT_TRACK_ID,
    val availableTracks: List<MusicTrack> = MusicRepository.availableTracks,
    val musicPosition: Long = 0L,

    // Ambient Sounds
    val activeAmbientSoundIds: Set<String> = emptySet(),
    val availableAmbientSounds: List<AmbientSound> = AmbientSoundRepository.availableSounds,

    // UI Modes & Toggles
    val isCompactMode: Boolean = false,
    val isCompactMenuExpanded: Boolean = false,
    val activeCompactSection: CompactSection? = null,
    val isNotificationEnabled: Boolean = true,

    // Settings State
    val isSettingsVisible: Boolean = false,
    val editingWorkMinutes: String = "25",
    val editingBreakMinutes: String = "5",
    val settingsError: String = "",

    // Exit Modal
    val isExitModalVisible: Boolean = false,

    // Mandatory Task Modal
    val isMandatoryTaskModalVisible: Boolean = false,
    
    // Focus Score
    val focusScoreResult: FocusScoreResult? = null,
    
    // Focus Tree Reward
    val focusTreeReward: Int? = null,
    val focusTree: FocusTreeRecord? = FocusTreeRecord(),
    val focusTreeAnimationEvent: FocusTreeAnimationEvent? = null,
    val focusTreeGrowthResult: FocusTreeGrowthResult? = null,
    val isFocusTreeGrowthAnimationVisible: Boolean = false
)