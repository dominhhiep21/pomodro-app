package thong.kotlin.pomodoro.features.focus.tree.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import thong.kotlin.pomodoro.core.config.AppConfig.DEFAULT_FOCUS_TREE_ID
import thong.kotlin.pomodoro.database.AuraDatabase
import thong.kotlin.pomodoro.features.focus.tree.domain.FocusTreeRecord
import thong.kotlin.pomodoro.features.focus.tree.domain.TreeGrowthStage
import thong.kotlin.pomodoro.features.focus.tree.domain.growAfterWorkCompleted
import thong.kotlin.pomodoro.features.focus.tree.domain.toDomain
import kotlin.time.Clock

class FocusTreeRepository(
    database: AuraDatabase? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val queries = database?.focusTreeLocalQueries

    suspend fun initFocusTreeIfNeeded() {
        withContext(ioDispatcher) {
            val existingTree = queries?.getFocusTree(DEFAULT_FOCUS_TREE_ID)?.executeAsOneOrNull()

            if (existingTree == null) {
                val now = Clock.System.now().toEpochMilliseconds()

                queries?.insertFocusTree(
                    tree_id = DEFAULT_FOCUS_TREE_ID,
                    growth_point = 0,
                    growth_stage = TreeGrowthStage.SEED.name,
                    total_completed_work_rounds = 0,
                    total_focus_seconds = 0,
                    average_focus_score = 0,
                    current_streak_days = 0,
                    last_growth_at_millis = null,
                    created_at_millis = now,
                    updated_at_millis = now
                )
            }
        }
    }

    suspend fun getFocusTree(): FocusTreeRecord {
        return withContext(ioDispatcher) {
            initFocusTreeIfNeeded()

            queries?.getFocusTree(DEFAULT_FOCUS_TREE_ID)
                ?.executeAsOne()
                ?.toDomain() ?: FocusTreeRecord()
        }
    }

    suspend fun growTreeAfterWorkCompleted(
        focusScore: Int,
        focusSeconds: Int
    ): FocusTreeRecord {
        return withContext(ioDispatcher) {
            initFocusTreeIfNeeded()

            val currentTree = queries?.getFocusTree(DEFAULT_FOCUS_TREE_ID)
                ?.executeAsOne()
                ?.toDomain() ?: FocusTreeRecord()

            val updatedTree = currentTree.growAfterWorkCompleted(
                focusScore = focusScore,
                focusSeconds = focusSeconds
            )

            queries?.updateFocusTree(
                growth_point = updatedTree.growthPoint.toLong(),
                growth_stage = updatedTree.growthStage.name,
                total_completed_work_rounds = updatedTree.totalCompletedWorkRounds.toLong(),
                total_focus_seconds = updatedTree.totalFocusSeconds.toLong(),
                average_focus_score = updatedTree.averageFocusScore.toLong(),
                last_growth_at_millis = updatedTree.lastGrowthAtMillis,
                updated_at_millis = updatedTree.updatedAtMillis,
                tree_id = updatedTree.treeId
            )

            updatedTree
        }
    }

    suspend fun resetFocusTree() {
        withContext(ioDispatcher) {
            queries?.deleteFocusTree(DEFAULT_FOCUS_TREE_ID)
            initFocusTreeIfNeeded()
        }
    }
}