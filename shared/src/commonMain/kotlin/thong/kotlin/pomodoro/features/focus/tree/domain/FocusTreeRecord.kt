package thong.kotlin.pomodoro.features.focus.tree.domain

import thong.kotlin.pomodoro.core.config.AppConfig.DEFAULT_FOCUS_TREE_ID
import thong.kotlin.pomodoro.database.Focus_tree_local
import kotlin.time.Clock

data class FocusTreeRecord(
    val treeId: String = DEFAULT_FOCUS_TREE_ID,
    val growthPoint: Int = 0,
    val growthStage: TreeGrowthStage = TreeGrowthStage.SEED,
    val totalCompletedWorkRounds: Int = 0,
    val totalFocusSeconds: Int = 0,
    val averageFocusScore: Int = 0,
    val currentStreakDays: Int = 0,
    val lastGrowthAtMillis: Long? = null,
    val createdAtMillis: Long = Clock.System.now().toEpochMilliseconds(),
    val updatedAtMillis: Long = Clock.System.now().toEpochMilliseconds()
)


fun Focus_tree_local.toDomain(): FocusTreeRecord {
    return FocusTreeRecord(
        treeId = tree_id,
        growthPoint = growth_point.toInt(),
        growthStage = TreeGrowthStage.valueOf(growth_stage),
        totalCompletedWorkRounds = total_completed_work_rounds.toInt(),
        totalFocusSeconds = total_focus_seconds.toInt(),
        averageFocusScore = average_focus_score.toInt(),
        currentStreakDays = current_streak_days.toInt(),
        lastGrowthAtMillis = last_growth_at_millis,
        createdAtMillis = created_at_millis,
        updatedAtMillis = updated_at_millis
    )
}

enum class TreeGrowthStage {
    SEED,           // Hạt giống
    SPROUT,         // Mầm cây
    SMALL_TREE,     // Cây nhỏ
    BIG_TREE,       // Cây lớn
    BLOOMING_TREE   // Cây nở hoa
}

fun calculateGrowthPoint(focusScore: Int): Int {
    return when {
        focusScore >= 90 -> 35
        focusScore >= 75 -> 25
        focusScore >= 60 -> 15
        focusScore >= 40 -> 8
        focusScore >= 20 -> 4
        focusScore >= 10 -> 2
        focusScore > 0 -> 1
        else -> 0
    }
}

fun getTreeGrowthStage(growthPoint: Int): TreeGrowthStage {
    return when {
        growthPoint >= 1500 -> TreeGrowthStage.BLOOMING_TREE
        growthPoint >= 700 -> TreeGrowthStage.BIG_TREE
        growthPoint >= 300 -> TreeGrowthStage.SMALL_TREE
        growthPoint >= 100 -> TreeGrowthStage.SPROUT
        else -> TreeGrowthStage.SEED
    }
}

fun FocusTreeRecord.growAfterWorkCompletedWithResult(
    focusScore: Int,
    focusSeconds: Int
): FocusTreeGrowthResult {
    val oldTree = this
    val addedGrowthPoint = calculateGrowthPoint(focusScore)
    val newGrowthPoint = growthPoint + addedGrowthPoint
    val newCompletedRounds = totalCompletedWorkRounds + 1
    val newTotalFocusSeconds = totalFocusSeconds + focusSeconds


    val newAverageFocusScore =
        ((averageFocusScore * totalCompletedWorkRounds) + focusScore) / newCompletedRounds

    val now = Clock.System.now().toEpochMilliseconds()

    val newTree = copy(
        growthPoint = newGrowthPoint,
        growthStage = getTreeGrowthStage(newGrowthPoint),
        totalCompletedWorkRounds = newCompletedRounds,
        totalFocusSeconds = newTotalFocusSeconds,
        averageFocusScore = newAverageFocusScore,
        lastGrowthAtMillis = now,
        updatedAtMillis = now
    )

    return FocusTreeGrowthResult(
        oldTree = oldTree,
        newTree = newTree,
        addedGrowthPoint = addedGrowthPoint,
        focusScore = focusScore,
        stageChanged = oldTree.growthStage != newTree.growthStage
    )
}