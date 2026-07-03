package thong.kotlin.pomodoro.features.pomodoro.task.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import thong.kotlin.pomodoro.core.utils.toEnumOrDefault
import thong.kotlin.pomodoro.core.utils.toMillisFromDateTimeText
import thong.kotlin.pomodoro.core.utils.toMillisFromDateTimeTextOrNull
import thong.kotlin.pomodoro.database.Session_task
import thong.kotlin.pomodoro.features.session.domain.SyncStatus
import kotlin.time.Clock

@Immutable
@Serializable
data class SessionTask(
    val taskId: String = "task_${Clock.System.now().toEpochMilliseconds()}",
    val sessionId: String,
    val title: String,
    val isCompleted: Boolean = false,
    val focusSeconds: Long = 0L,
    val createdAtMillis: Long = Clock.System.now().toEpochMilliseconds(),
    val updatedAtMillis: Long = Clock.System.now().toEpochMilliseconds(),
    val completedAtMillis: Long? = null,
    val syncStatus: SyncStatus = SyncStatus.LOCAL_ONLY
)

fun Session_task.toSessionTaskRecord(): SessionTask {
    return SessionTask(
        taskId = task_id,
        sessionId = session_id,
        title = title,
        isCompleted = is_completed.toInt() != 0,
        focusSeconds = focus_seconds,
        createdAtMillis = created_at.toMillisFromDateTimeText(),
        updatedAtMillis = updated_at.toMillisFromDateTimeText(),
        completedAtMillis = completed_at.toMillisFromDateTimeTextOrNull(),
        syncStatus = sync_status.toEnumOrDefault(SyncStatus.LOCAL_ONLY)
    )
}

