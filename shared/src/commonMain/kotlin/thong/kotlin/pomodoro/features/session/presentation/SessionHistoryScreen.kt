package thong.kotlin.pomodoro.features.session.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import thong.kotlin.pomodoro.core.designsystem.components.AuraBackground
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.core.media.SoundManager
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.SessionRecord

class SessionHistoryScreen(
    soundManager: SoundManager? = null
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        SessionListUI(getMockSession(), navigator)
    }
}

@Composable
private fun SessionListUI(mockSessions: List<SessionRecord>, navigator: Navigator) {
    AuraBackground {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "DANH SÁCH PHIÊN LÀM VIỆC",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Unspecified,
                        navigationIconContentColor = Color.Unspecified,
                        titleContentColor = Color.Unspecified,
                        actionIconContentColor = Color.Unspecified
                    )
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
            ) {
                SummaryHeader()

                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(mockSessions) { session ->
                        SessionItem(session)
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryHeader() {
    GlassBox(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        backgroundColor = AuraColors.BottomBarBackground.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Tổng thời gian", color = AuraColors.TextSecondary, fontSize = 12.sp)
                Text(
                    "12h 45m",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                Icons.Default.History,
                contentDescription = null,
                tint = AuraColors.WorkMode,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
private fun SessionItem(session: SessionRecord) {
    val statusColor = when (session.status) {
        "COMPLETED" -> AuraColors.WorkMode
        "CANCELLED" -> Color.Red.copy(alpha = 0.7f)
        else -> AuraColors.TextSecondary
    }

    GlassBox(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        backgroundColor = AuraColors.BottomBarBackground.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (session.mode == "WORK") "Phiên tập trung" else "Nghỉ giải lao",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = session.startTime,
                    color = AuraColors.TextSecondary,
                    fontSize = 11.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${session.durationMinutes}m",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = session.status,
                    color = statusColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun getMockSession() = listOf(
    SessionRecord("1", "2023-10-01 08:00", "2023-10-01 08:25", "WORK", 25, "COMPLETED", 2),
    SessionRecord(
        "2",
        "2023-10-01 08:25",
        "2023-10-01 08:30",
        "SHORT_BREAK",
        5,
        "COMPLETED"
    ),
    SessionRecord("3", "2023-10-01 08:30", "2023-10-01 08:55", "WORK", 25, "COMPLETED", 1),
    SessionRecord("4", "2023-10-01 09:00", "2023-10-01 09:10", "WORK", 10, "CANCELLED"),
    SessionRecord("5", "2023-09-30 20:00", "2023-09-30 20:25", "WORK", 25, "COMPLETED", 4)
)