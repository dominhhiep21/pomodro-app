package thong.kotlin.pomodoro.features.focus.score.domain

import thong.kotlin.pomodoro.features.session.data.LearningSessionManager

class FocusScoreManager(
    private val learningSessionManager: LearningSessionManager,
) {

    /**
     * Analyze a completed or paused session to get the Focus Score.
     */
    suspend fun getScoreForSession(sessionId: String): FocusScoreResult? {
        val session = learningSessionManager.getSessionById(sessionId) ?: return null
        
        // In a real implementation, we would query the database for events.
        // Since we don't have a direct 'getEventsBySessionId' in Manager yet, 
        // we'll assume the manager/repository is extended or use a mock logic for now.
        // For this task, I will provide the calculation logic.
        
        // Mocking event counts for now as the infrastructure doesn't fully support event retrieval yet.
        // Ideally: val events = learningSessionManager.getEventsBySessionId(sessionId)
        val pausedCount = 0 // events.count { it.eventType == LearningSessionEventType.WORK_ROUND_PAUSED }
        val skippedCount = 0 // events.count { it.eventType == LearningSessionEventType.WORK_ROUND_SKIPPED }

        return FocusScoreCalculator.calculate(
            session = session,
            pausedCount = pausedCount,
            skippedCount = skippedCount
        )
    }
}
