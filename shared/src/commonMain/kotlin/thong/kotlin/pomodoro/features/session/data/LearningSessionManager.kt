package thong.kotlin.pomodoro.features.session.data

import thong.kotlin.pomodoro.features.session.domain.LearningSessionRecord
import thong.kotlin.pomodoro.features.session.domain.LearningSessionStatus
import kotlin.time.Clock

class LearningSessionManager(
    private val repository: LearningSessionRepository
) {

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

    fun getAllLearningSession() = repository.getAllLearningSessionRecords()

    fun getTotalFocusSeconds() = repository.getTotalFocusSeconds()

    fun insertSession(session: LearningSessionRecord) = repository.insertSession(session)

    fun updateSession(session: LearningSessionRecord) = repository.updateSession(session)

    fun clearAllSessionsData() = repository.clearAllSessionsData()

    private fun currentTimeMillis(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }
}