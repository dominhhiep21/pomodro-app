package thong.kotlin.pomodoro.features.focus.tree.domain

data class FocusTreeGrowthResult(
    val oldTree: FocusTreeRecord,
    val newTree: FocusTreeRecord,
    val addedGrowthPoint: Int,
    val focusScore: Int,
    val stageChanged: Boolean
)
