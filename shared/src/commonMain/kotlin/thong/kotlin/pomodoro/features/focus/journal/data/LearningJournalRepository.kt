package thong.kotlin.pomodoro.features.focus.journal.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import thong.kotlin.pomodoro.database.AuraDatabase
import thong.kotlin.pomodoro.features.focus.journal.domain.JournalEntry
import thong.kotlin.pomodoro.features.focus.journal.domain.toDomain

class LearningJournalRepository(
    database: AuraDatabase? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val queries = database?.learningJournalQueries

    suspend fun insertJournalEntry(entry: JournalEntry) {
        withContext(ioDispatcher) {
            queries?.insertJournalEntry(
                id = entry.id,
                session_id = entry.sessionId,
                work_round = entry.workRound.toLong(),
                achievements = entry.achievements,
                focus_rating = entry.focusRating.toLong(),
                created_at_millis = entry.createdAtMillis,
                sync_status = entry.syncStatus.name
            )
        }
    }

    suspend fun getJournalEntriesBySessionId(sessionId: String): List<JournalEntry> {
        return withContext(ioDispatcher) {
            queries?.selectJournalsBySessionId(sessionId)
                ?.executeAsList()
                ?.map { it.toDomain() } ?: emptyList()
        }
    }

    suspend fun deleteJournalEntriesBySessionId(sessionId: String) {
        withContext(ioDispatcher) {
            queries?.deleteJournalsBySessionId(sessionId)
        }
    }

    suspend fun deleteAllJournalEntries() {
        withContext(ioDispatcher) {
            queries?.deleteAllJournals()
        }
    }
}
