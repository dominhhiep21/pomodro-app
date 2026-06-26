package thong.kotlin.pomodoro.features.session.data

import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.LearningSessionState
import thong.kotlin.pomodoro.features.session.domain.LearningSessionStatus
import kotlin.time.Clock

class LearningSessionManager(
    private val repository: LearningSessionRepository
) {

    fun startSession(workMinutes: Int) {
        val now = currentTimeMillis()

        val newSession = LearningSessionState(
            sessionId = "session_$now",
            status = LearningSessionStatus.RUNNING,
            remainingSeconds = workMinutes * 60,
            currentRound = 1,
            startedAtMillis = now,
            pausedAtMillis = null,
            endedAtMillis = null,
            completedByUser = false
        )

        repository.saveCurrentSession(newSession)
    }

    fun pauseSessionBecauseAppInactive() {
        val current = repository.getCurrentSession()

        if (current.status != LearningSessionStatus.RUNNING) {
            return
        }

        repository.saveCurrentSession(
            current.copy(
                status = LearningSessionStatus.PAUSED,
                pausedAtMillis = currentTimeMillis()
            )
        )
    }

    fun resumeSession() {
        val current = repository.getCurrentSession()

        if (current.status != LearningSessionStatus.PAUSED) {
            return
        }

        repository.saveCurrentSession(
            current.copy(
                status = LearningSessionStatus.RUNNING,
                pausedAtMillis = null
            )
        )
    }

    fun finishSessionByUser() {
        val current = repository.getCurrentSession()

        if (
            current.status != LearningSessionStatus.RUNNING &&
            current.status != LearningSessionStatus.PAUSED
        ) {
            return
        }

        val finishedSession = current.copy(
            status = LearningSessionStatus.COMPLETED,
            endedAtMillis = currentTimeMillis(),
            completedByUser = true
        )

        repository.saveCompletedSession(finishedSession)
        repository.clearCurrentSession()
    }

    fun onTick() {
        val current = repository.getCurrentSession()

        if (current.status != LearningSessionStatus.RUNNING) {
            return
        }

        val newRemainingSeconds = current.remainingSeconds - 1

        repository.saveCurrentSession(
            current.copy(
                remainingSeconds = newRemainingSeconds.coerceAtLeast(0)
            )
        )
    }

    private fun currentTimeMillis(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }
}