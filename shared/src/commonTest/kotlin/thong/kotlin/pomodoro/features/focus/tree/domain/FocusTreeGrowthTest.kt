package thong.kotlin.pomodoro.features.focus.tree.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FocusTreeGrowthTest {

    // ===== calculateGrowthPoint =====

    @Test
    fun calculateGrowthPoint_score90Plus_returns35() {
        val actualPoints = calculateGrowthPoint(95)
        assertEquals(35, actualPoints)
    }

    @Test
    fun calculateGrowthPoint_score75_returns25() {
        val actualPoints = calculateGrowthPoint(75)
        assertEquals(25, actualPoints)
    }

    @Test
    fun calculateGrowthPoint_score60_returns15() {
        val actualPoints = calculateGrowthPoint(60)
        assertEquals(15, actualPoints)
    }

    @Test
    fun calculateGrowthPoint_score40_returns8() {
        val actualPoints = calculateGrowthPoint(40)
        assertEquals(8, actualPoints)
    }

    @Test
    fun calculateGrowthPoint_score20_returns4() {
        val actualPoints = calculateGrowthPoint(20)
        assertEquals(4, actualPoints)
    }

    @Test
    fun calculateGrowthPoint_score10_returns2() {
        val actualPoints = calculateGrowthPoint(10)
        assertEquals(2, actualPoints)
    }

    @Test
    fun calculateGrowthPoint_score5_returns1() {
        val actualPoints = calculateGrowthPoint(5)
        assertEquals(1, actualPoints)
    }

    @Test
    fun calculateGrowthPoint_score0_returns50() {
        val actualPoints = calculateGrowthPoint(0)
        assertEquals(50, actualPoints)
    }

    // ===== getTreeGrowthStage =====

    @Test
    fun getTreeGrowthStage_1500Plus_returnsBloomingTree() {
        assertEquals(TreeGrowthStage.BLOOMING_TREE, getTreeGrowthStage(1500))
    }

    @Test
    fun getTreeGrowthStage_700_returnsBigTree() {
        assertEquals(TreeGrowthStage.BIG_TREE, getTreeGrowthStage(700))
    }

    @Test
    fun getTreeGrowthStage_300_returnsSmallTree() {
        assertEquals(TreeGrowthStage.SMALL_TREE, getTreeGrowthStage(300))
    }

    @Test
    fun getTreeGrowthStage_100_returnsSprout() {
        assertEquals(TreeGrowthStage.SPROUT, getTreeGrowthStage(100))
    }

    @Test
    fun getTreeGrowthStage_99_returnsSeed() {
        assertEquals(TreeGrowthStage.SEED, getTreeGrowthStage(99))
    }

    // ===== growAfterWorkCompletedWithResult =====

    @Test
    fun growAfterWorkCompleted_highScore_addsCorrectGrowthPoints() {
        // Given
        val tree = FocusTreeRecord(
            growthPoint = 50,
            growthStage = TreeGrowthStage.SEED,
            totalCompletedWorkRounds = 5,
            totalFocusSeconds = 7500,
            averageFocusScore = 80
        )

        // When
        val result = tree.growAfterWorkCompletedWithResult(focusScore = 90, focusSeconds = 1500)

        // Then
        assertEquals(85, result.newTree.growthPoint) // 50 + 35
        assertEquals(6, result.newTree.totalCompletedWorkRounds)
        assertEquals(9000, result.newTree.totalFocusSeconds)
        assertEquals(35, result.addedGrowthPoint)
    }

    @Test
    fun growAfterWorkCompleted_crossesStageThreshold_stageChangedTrue() {
        // Given
        val tree = FocusTreeRecord(
            growthPoint = 95,
            growthStage = TreeGrowthStage.SEED,
            totalCompletedWorkRounds = 3,
            totalFocusSeconds = 4500,
            averageFocusScore = 85
        )

        // When
        val result = tree.growAfterWorkCompletedWithResult(focusScore = 90, focusSeconds = 1500)

        // Then — 95 + 35 = 130 → SPROUT
        assertEquals(130, result.newTree.growthPoint)
        assertEquals(TreeGrowthStage.SPROUT, result.newTree.growthStage)
        assertTrue(result.stageChanged)
    }

    @Test
    fun growAfterWorkCompleted_noStageChange_stageChangedFalse() {
        // Given
        val tree = FocusTreeRecord(
            growthPoint = 50,
            growthStage = TreeGrowthStage.SEED,
            totalCompletedWorkRounds = 2,
            totalFocusSeconds = 3000,
            averageFocusScore = 70
        )

        // When
        val result = tree.growAfterWorkCompletedWithResult(focusScore = 60, focusSeconds = 1500)

        // Then — 50 + 15 = 65 → still SEED
        assertEquals(65, result.newTree.growthPoint)
        assertEquals(TreeGrowthStage.SEED, result.newTree.growthStage)
        assertFalse(result.stageChanged)
    }

    @Test
    fun growAfterWorkCompleted_updatesAverageFocusScore() {
        // Given
        val tree = FocusTreeRecord(
            growthPoint = 200,
            growthStage = TreeGrowthStage.SPROUT,
            totalCompletedWorkRounds = 4,
            totalFocusSeconds = 6000,
            averageFocusScore = 80
        )

        // When
        val result = tree.growAfterWorkCompletedWithResult(focusScore = 60, focusSeconds = 1500)

        // Then — newAvg = (80*4 + 60) / 5 = 76
        assertEquals(76, result.newTree.averageFocusScore)
    }
}
