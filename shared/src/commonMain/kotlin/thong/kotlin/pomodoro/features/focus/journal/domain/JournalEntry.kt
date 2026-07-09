package thong.kotlin.pomodoro.features.focus.journal.domain

import kotlinx.serialization.Serializable
import thong.kotlin.pomodoro.core.utils.toEnumOrDefault
import thong.kotlin.pomodoro.database.Learning_journal
import thong.kotlin.pomodoro.features.session.domain.SyncStatus
import kotlin.time.Clock

@Serializable
data class JournalEntry(
    val id: String = "journal_${Clock.System.now().toEpochMilliseconds()}",
    val sessionId: String,
    val workRound: Int,
    val achievements: String,
    val focusRating: Int, // 1 to 5
    val createdAtMillis : Long = Clock.System.now().toEpochMilliseconds(),
    val syncStatus: SyncStatus = SyncStatus.LOCAL_ONLY
)

fun Learning_journal.toDomain(): JournalEntry {
    return JournalEntry(
        id = id,
        sessionId = session_id,
        workRound = work_round.toInt(),
        achievements = achievements,
        focusRating = focus_rating.toInt(),
        createdAtMillis = created_at_millis,
        syncStatus = sync_status.toEnumOrDefault(SyncStatus.LOCAL_ONLY)
    )
}
