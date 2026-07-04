package thong.kotlin.pomodoro.features.session.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
import thong.kotlin.pomodoro.features.learning.mode.domain.LearningStyle
import thong.kotlin.pomodoro.features.pomodoro._base.PomodoroScreenV2
import thong.kotlin.pomodoro.features.pomodoro._base.domain.HistoryFilter
import thong.kotlin.pomodoro.features.pomodoro.ambient.data.AmbientSoundRepository
import thong.kotlin.pomodoro.features.pomodoro.music.data.MusicRepository
import thong.kotlin.pomodoro.features.session.domain.CurrentLearningMode
import thong.kotlin.pomodoro.features.session.domain.LearningSessionRecord
import thong.kotlin.pomodoro.features.session.domain.color
import thong.kotlin.pomodoro.features.session.domain.toDisplayText
import thong.kotlin.pomodoro.features.background.data.BackgroundRepository
import thong.kotlin.pomodoro.features.startup.presentation.HomeScreenV2

class SessionHistoryScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val learningSessionManager = remember { DependencyRegistry.learningSessionManager }

        var sessions by remember { mutableStateOf(emptyList<LearningSessionRecord>()) }
        var isLoading by remember { mutableStateOf(true) }
        var totalFocusSeconds by remember { mutableLongStateOf(0L) }

        var selectedFilter by remember { mutableStateOf(HistoryFilter.ALL) }
        var sessionToContinue by remember { mutableStateOf<LearningSessionRecord?>(null) }

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

        val filteredSessions = when (selectedFilter) {
            HistoryFilter.ALL -> sessions
            HistoryFilter.FOCUS -> sessions.filter { it.currentLearningMode == CurrentLearningMode.WORK }
            HistoryFilter.SHORT_BREAK -> sessions.filter { it.currentLearningMode == CurrentLearningMode.BREAK }
            HistoryFilter.LONG_BREAK -> sessions.filter { it.currentLearningMode == CurrentLearningMode.LONG_BREAK }
        }

        if (sessionToContinue != null) {
            SessionContinuationModal(
                session = sessionToContinue!!,
                onConfirm = {
                    val session = sessionToContinue!!
                    sessionToContinue = null
                    navigator.push(
                        PomodoroScreenV2(
                            learningStyle = session.sessionMode,
                            currentSessionId = session.sessionId
                        )
                    )
                },
                onDismiss = { sessionToContinue = null }
            )
        }

        SessionListUI(
            isListLoading = isLoading,
            sessions = filteredSessions,
            totalFocusSeconds = totalFocusSeconds,
            selectedFilter = selectedFilter,
            onFilterSelect = { selectedFilter = it },
            onBack = {
                navigator.replace(HomeScreenV2())
            },
            onCreateSession = {
                navigator.push(LearningStyleScreen("session_history"))
            },
            onSessionClick = { session ->
                sessionToContinue = session
            }
        )
    }
}

@Composable
private fun SessionContinuationModal(
    session: LearningSessionRecord,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        GlassBox(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(28.dp),
            backgroundColor = AuraColors.BottomBarBackground.copy(alpha = 0.95f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon Header
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(AuraColors.WorkMode.copy(alpha = 0.1f), CircleShape)
                        .border(1.dp, AuraColors.WorkMode.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            session.sessionMode == LearningStyle.GROUP -> Icons.Default.Groups
                            session.currentLearningMode == CurrentLearningMode.WORK -> Icons.Default.Eco
                            else -> Icons.Default.Coffee
                        },
                        contentDescription = null,
                        tint = AuraColors.WorkMode,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    "Tiếp tục phiên học?",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Bạn có muốn tiếp tục vào phiên làm việc này không?",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Session Details
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailRow("Chế độ", session.sessionMode.toDisplayText())
                    DetailRow("Bắt đầu lúc", session.startedAtMillis.toDateTimeText())
                    DetailRow("Tổng thời gian", secondsToMinutesText(session.totalFocusSeconds))
                    DetailRow("Số vòng hoàn thành", "${session.completedWorkRounds}")
                    DetailRow("Background", BackgroundRepository.getNameById(session.lastBackgroundId.toString()))
                    DetailRow("Music", MusicRepository.getNameById(session.lastMusicId.toString()))
                    DetailRow("Ambient Sound", AmbientSoundRepository.getNameById(session.lastAmbientSounds.toList()).toString())
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color.White.copy(alpha = 0.2f)
                        )
                    ) {
                        Text("Để sau", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AuraColors.WorkMode)
                    ) {
                        Text("Tiếp tục", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SessionListUI(
    isListLoading: Boolean,
    sessions: List<LearningSessionRecord>,
    totalFocusSeconds: Long,
    selectedFilter: HistoryFilter,
    onFilterSelect: (HistoryFilter) -> Unit,
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
                                "Lịch sử phiên học",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .padding(start = 12.dp)
                                    .size(40.dp)
                                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    "Back",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
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

                    FilterTabs(
                        selectedFilter = selectedFilter,
                        onFilterSelect = onFilterSelect
                    )

                    Spacer(modifier = Modifier.height(16.dp))

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
private fun FilterTabs(
    selectedFilter: HistoryFilter,
    onFilterSelect: (HistoryFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HistoryFilter.entries.forEach { filter ->
            val isSelected = selectedFilter == filter
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) Color.White.copy(alpha = 0.1f) else Color.Transparent)
                    .border(
                        1.dp,
                        if (isSelected) Color.White.copy(alpha = 0.3f) else Color.Transparent,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onFilterSelect(filter) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = filter.label,
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon in box
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    tint = AuraColors.LongBreakMode,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Tổng thời gian tập trung",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
                Text(
                    text = secondsToHourMinuteText(totalFocusSeconds),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Mock Chart
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(50.dp)
            ) {
                MockChart()
            }
        }
    }
}

@Composable
private fun MockChart() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val barWidth = 6.dp.toPx()
        val spacing = 4.dp.toPx()
        val heights = listOf(0.3f, 0.5f, 0.4f, 0.7f, 1.0f, 0.8f, 0.6f, 0.5f)

        heights.forEachIndexed { index, h ->
            val left = index * (barWidth + spacing)
            drawRoundRect(
                color = if (index == 4) AuraColors.LongBreakMode else AuraColors.LongBreakMode.copy(
                    alpha = 0.4f
                ),
                topLeft = Offset(left, size.height * (1f - h)),
                size = Size(barWidth, size.height * h),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
            )
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
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
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

                // See more footer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Xem thêm",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionItem(
    session: LearningSessionRecord,
    onClick: () -> Unit
) {
    val statusColor = session.status.color()
    val modeIcon = when {
        session.sessionMode == LearningStyle.GROUP -> Icons.Default.Groups
        session.currentLearningMode == CurrentLearningMode.WORK -> Icons.Default.Eco
        else -> Icons.Default.Coffee
    }

    val modeColor = when {
        session.sessionMode == LearningStyle.GROUP -> AuraColors.SessionIdleMode
        session.currentLearningMode == CurrentLearningMode.WORK -> AuraColors.ShortBreakMode
        else -> AuraColors.LongBreakMode
    }

    GlassBox(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = AuraColors.BottomBarBackground.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading Icon box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(modeColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .border(1.dp, modeColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modeIcon,
                    contentDescription = null,
                    tint = modeColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (session.sessionMode == LearningStyle.GROUP) "Phiên làm việc nhóm" else session.currentLearningMode.toDisplayText(),
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = session.startedAtMillis.toDateTimeText(), // Placeholder for actual start time if not in toDateTimeText
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 12.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = secondsToMinutesText(session.totalFocusSeconds),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Status Badge
                Box(
                    modifier = Modifier
                        .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                ) {
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
}