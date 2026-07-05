package thong.kotlin.pomodoro.features.focus.tree.presentation.animation

import thong.kotlin.pomodoro.features.focus.tree.domain.TreeGrowthStage
import kotlin.time.Clock

data class FocusTreeAnimationEvent(
    val id: Long = Clock.System.now().toEpochMilliseconds(),
    val addedGrowthPoint: Int,
    val focusScore: Int,
    val oldStage: TreeGrowthStage,
    val newStage: TreeGrowthStage,
    val stageChanged: Boolean
)