package thong.kotlin.pomodoro.features.focus.journal.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.features.focus.journal.domain.JournalEntry

@Composable
fun SessionJournalSummary(
    entries: List<JournalEntry>,
    modifier: Modifier = Modifier
) {
    if (entries.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Nhật ký phiên học",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        entries.forEach { entry ->
            JournalEntryItem(entry = entry)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun JournalEntryItem(entry: JournalEntry) {
    GlassBox(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color.White.copy(alpha = 0.05f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Phiên ${entry.workRound}",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (index < entry.focusRating) Color(0xFFFFD700) else Color.White.copy(alpha = 0.1f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (entry.achievements.isBlank()) "Không có nội dung ghi chép" else entry.achievements,
                color = Color.White,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )
        }
    }
}
