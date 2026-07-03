package thong.kotlin.pomodoro.features.session.domain

import kotlinx.serialization.json.Json
import thong.kotlin.pomodoro.database.SessionHistoryLocalQueries
import kotlin.time.Clock

data class LearningSessionEvent(
    val eventId: String = "event_manual_${Clock.System.now().toEpochMilliseconds()}",
    val sessionId: String,
    val eventType: LearningSessionEventType,
    val occurredAtMillis: Long = Clock.System.now().toEpochMilliseconds(),
    val backgroundId: String? = null,
    val musicId: String? = null,
    val ambientSoundId: List<String>? = null,
    val remainingSeconds: Int? = null,
    val currentRound: Int = 0,
    val metadata: Map<String, String> = emptyMap(),
    val syncStatus: SyncStatus = SyncStatus.LOCAL_ONLY
)

fun LearningSessionEvent.insertInto(
    queries: SessionHistoryLocalQueries
) {
    queries.insertLearningSessionEvent(
        event_id = eventId,
        session_id = sessionId,
        event_type = eventType.name,
        occurred_at_millis = occurredAtMillis.toString(),
        background_id = backgroundId,
        music_id = musicId,
        ambient_sound_ids_json = ambientSoundId?.let { Json.encodeToString(it) },
        remaining_seconds = remainingSeconds?.toLong(),
        current_round = currentRound.toLong(),
        metadata_json = Json.encodeToString(metadata),
        sync_status = syncStatus.name
    )
}