package thong.kotlin.pomodoro.features.session.domain

import kotlinx.serialization.json.Json
import thong.kotlin.pomodoro.database.SessionHistoryLocalQueries

data class LearningSessionEvent(
    val eventId: String,
    val sessionId: String,
    val eventType: LearningSessionEventType,
    val occurredAtMillis: Long,
    val backgroundId: String?,
    val musicId: String?,
    val ambientSoundId: List<String>?,
    val remainingSeconds: Int?,
    val currentRound: Int?,
    val metadata: Map<String, String> = emptyMap(),
    val syncStatus: SyncStatus
)

fun LearningSessionEvent.insertInto(
    queries: SessionHistoryLocalQueries
) {
    queries.insertLearningSessionEvent(
        event_id = eventId,
        session_id = sessionId,
        event_type = eventType.name,
        occurred_at_millis = occurredAtMillis,
        background_id = backgroundId,
        music_id = musicId,
        ambient_sound_ids_json = ambientSoundId?.let { Json.encodeToString(it) },
        remaining_seconds = remainingSeconds?.toLong(),
        current_round = currentRound?.toLong(),
        metadata_json = Json.encodeToString(metadata),
        sync_status = syncStatus.name
    )
}