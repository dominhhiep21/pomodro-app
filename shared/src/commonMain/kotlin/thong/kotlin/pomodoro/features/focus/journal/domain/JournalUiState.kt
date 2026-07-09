package thong.kotlin.pomodoro.features.focus.journal.domain

import kotlinx.serialization.Serializable

@Serializable
data class JournalUiState(
    val isJournalModalVisible: Boolean = false,
    val entries: List<JournalEntry> = emptyList()
)
