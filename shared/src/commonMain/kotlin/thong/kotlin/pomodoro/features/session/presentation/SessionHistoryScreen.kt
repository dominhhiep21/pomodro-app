package thong.kotlin.pomodoro.features.session.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import thong.kotlin.pomodoro.core.designsystem.components.AuraBackground
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.core.utils.secondsToHourMinuteText
import thong.kotlin.pomodoro.core.utils.secondsToMinutesText
import thong.kotlin.pomodoro.core.utils.toDateTimeText
import thong.kotlin.pomodoro.di.DependencyRegistry
import thong.kotlin.pomodoro.features.learning.mode.components.LearningStyleScreen
import thong.kotlin.pomodoro.features.pomodoro._base.PomodoroScreenV2
import thong.kotlin.pomodoro.features.session.domain.LearningSessionRecord
import thong.kotlin.pomodoro.features.session.domain.color
import thong.kotlin.pomodoro.features.session.domain.toDisplayText

class SessionHistoryScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val learningSessionManager = remember { DependencyRegistry.learningSessionManager }

        var sessions by remember { mutableStateOf(emptyList<LearningSessionRecord>()) }
        var isLoading by remember { mutableStateOf(true) }
        var totalFocusSeconds by remember { mutableLongStateOf(0L) }

        LaunchedEffect(Unit) {
            try {
                isLoading = true
                val sessionList = learningSessionManager.getAllLearningSession()
                val total = learningSessionManager.getTotalFocusSeconds()

                sessions = sessionList.sortedByDescending { it.startedAtMillis }
                totalFocusSeconds = total
            } catch (_: Exception) {
                sessions = emptyList()
                totalFocusSeconds = 0L
            } finally {
                isLoading = false
            }
        }

        SessionListUI(
            isListLoading = isLoading,
            sessions = sessions,
            totalFocusSeconds = totalFocusSeconds,
            onBack = {
                navigator.pop()
            },
            onCreateSession = {
                navigator.push(LearningStyleScreen())
            },
            onSessionClick = { session ->
                navigator.push(
                    PomodoroScreenV2(
                        learningStyle = session.sessionMode,
                        currentSessionId = session.sessionId
                    )
                )
            }
        )
    }
}

@Composable
private fun SessionListUI(
    isListLoading: Boolean,
    sessions: List<LearningSessionRecord>,
    totalFocusSeconds: Long,
    onBack: () -> Unit,
    onCreateSession: () -> Unit,
    onSessionClick: (LearningSessionRecord) -> Unit
) {
    BoxWithConstraints {
        val isLandscape = maxWidth > maxHeight

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
                            IconButton(onClick = onBack) {
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
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = onCreateSession,
                        containerColor = AuraColors.WorkMode,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Tạo phiên mới")
                    }
                },
                containerColor = Color.Transparent
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = if (isLandscape) 40.dp else 20.dp)
                ) {
                    SummaryHeader(totalFocusSeconds = totalFocusSeconds)

                    Spacer(modifier = Modifier.height(24.dp))

                    SessionListContent(
                        isListLoading = isListLoading,
                        sessions = sessions,
                        isLandscape = isLandscape,
                        onSessionClick = onSessionClick
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionListContent(
    isListLoading: Boolean,
    sessions: List<LearningSessionRecord>,
    isLandscape: Boolean,
    onSessionClick: (LearningSessionRecord) -> Unit
) {
    when {
        isListLoading -> {
            Text(
                text = "Đang tải dữ liệu...",
                color = AuraColors.TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        sessions.isEmpty() -> {
            Text(
                text = "Chưa có phiên làm việc nào",
                color = AuraColors.TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        else -> {
            if (isLandscape) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(
                        items = sessions,
                        key = { it.sessionId }
                    ) { session ->
                        SessionItem(
                            session = session,
                            onClick = { onSessionClick(session) }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(
                        items = sessions,
                        key = { it.sessionId }
                    ) { session ->
                        SessionItem(
                            session = session,
                            onClick = { onSessionClick(session) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryHeader(totalFocusSeconds: Long = 0L) {
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
                Text("Tổng thời gian focus", color = AuraColors.TextSecondary, fontSize = 12.sp)
                Text(
                    text = secondsToHourMinuteText(totalFocusSeconds),
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
private fun SessionItem(
    session: LearningSessionRecord,
    onClick: () -> Unit
) {
    val statusColor = session.status.color()

    GlassBox(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = AuraColors.BottomBarBackground.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${session.currentLearningMode.toDisplayText()} (${session.sessionMode.toDisplayText()})",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = session.startedAtMillis.toDateTimeText(),
                    color = AuraColors.TextSecondary,
                    fontSize = 11.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = secondsToMinutesText(session.totalFocusSeconds),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = session.status.toDisplayText(),
                    color = statusColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}