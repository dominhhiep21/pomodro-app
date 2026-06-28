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
import thong.kotlin.pomodoro.core.utils.secondsToHourMinuteText
import thong.kotlin.pomodoro.core.utils.secondsToMinutesText
import thong.kotlin.pomodoro.core.utils.toDateTimeText
import thong.kotlin.pomodoro.di.DependencyRegistry
import thong.kotlin.pomodoro.features.learning.mode.components.LearningStyleScreen
import thong.kotlin.pomodoro.features.session.domain.CurrentLearningMode
import thong.kotlin.pomodoro.features.session.domain.LearningSessionRecord
import thong.kotlin.pomodoro.features.session.domain.LearningSessionStatus
import thong.kotlin.pomodoro.features.session.domain.toLearningSessionRecord

class SessionHistoryScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val database = remember { DependencyRegistry.database }

        var sessions by remember { mutableStateOf(emptyList<LearningSessionRecord>()) }
        var totalFocusSeconds by remember { mutableLongStateOf(0L) }
        var showCreateModal by remember { mutableStateOf(false) }

        fun refreshData() {
            database?.sessionHistoryLocalQueries?.let { queries ->
                sessions = queries.selectAllSessionHistory().executeAsList().map { it.toLearningSessionRecord() }
                totalFocusSeconds = queries.selectTotalFocusSeconds().executeAsOne().toLong()
            }
        }

        LaunchedEffect(database) {
            refreshData()
        }

        SessionListUI(
            sessions = sessions,
            totalFocusSeconds = totalFocusSeconds,
            navigator = navigator,
            onCreateSession = {
                navigator.replace(LearningStyleScreen())
            }
        )

//        if (showCreateModal) {
//            CreateSessionModal(
//                onDismiss = { showCreateModal = false },
//                onConfirm = { workMin, breakMin ->
//                    database?.sessionHistoryLocalQueries?.let { queries ->
//                        val now = Clock.System.now().toEpochMilliseconds()
//                        val newSession = LearningSessionRecord(
//                            sessionId = "manual_$now",
//                            userId = null,
//                            anonymousUserId = null,
//                            sessionMode = LearningStyle.SOLO,
//                            status = LearningSessionStatus.COMPLETED,
//                            currentLearningMode = CurrentLearningMode.WORK,
//                            startedAtMillis = now,
//                            endedAtMillis = now + (workMin * 60 * 1000L),
//                            lastPausedAtMillis = null,
//                            plannedWorkMinutes = workMin,
//                            plannedBreakMinutes = breakMin,
//                            totalFocusSeconds = workMin * 60,
//                            syncStatus = SyncStatus.LOCAL_ONLY
//                        )
//                        newSession.insertInto(queries)
//                        refreshData()
//                    }
//                    showCreateModal = false
//                }
//            )
//        }
    }
}

@Composable
private fun SessionListUI(
    sessions: List<LearningSessionRecord>,
    totalFocusSeconds: Long,
    navigator: Navigator,
    onCreateSession: () -> Unit
) {
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
                    .padding(horizontal = 20.dp)
            ) {
                SummaryHeader(totalFocusSeconds = totalFocusSeconds)

                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(sessions) { session ->
                        SessionItem(session)
                    }
                }
            }
        }
    }
}

//@Composable
//private fun CreateSessionModal(
//    onDismiss: () -> Unit,
//    onConfirm: (workMin: Int, breakMin: Int) -> Unit
//) {
//    var workMinutes by remember { mutableStateOf("25") }
//    var breakMinutes by remember { mutableStateOf("5") }
//
//    Dialog(onDismissRequest = onDismiss) {
//        GlassBox(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            shape = RoundedCornerShape(24.dp),
//            backgroundColor = AuraColors.BottomBarBackground.copy(alpha = 0.9f)
//        ) {
//            Column(
//                modifier = Modifier.padding(24.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    "THÊM PHIÊN MỚI",
//                    color = Color.White,
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Black
//                )
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                InputFieldLabel("Thời gian làm việc (phút)")
//                AuraInputField(
//                    value = workMinutes,
//                    onValueChange = { if (it.all { char -> char.isDigit() }) workMinutes = it },
//                    placeholder = "25",
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                InputFieldLabel("Thời gian nghỉ (phút)")
//                AuraInputField(
//                    value = breakMinutes,
//                    onValueChange = { if (it.all { char -> char.isDigit() }) breakMinutes = it },
//                    placeholder = "5",
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                Spacer(modifier = Modifier.height(32.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    AuraButton(
//                        onClick = onDismiss,
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Text("Hủy", color = Color.White.copy(alpha = 0.7f))
//                    }
//                    AuraButton(
//                        onClick = {
//                            val w = workMinutes.toIntOrNull() ?: 25
//                            val b = breakMinutes.toIntOrNull() ?: 5
//                            onConfirm(w, b)
//                        },
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Text("Xác nhận", color = AuraColors.WorkMode, fontWeight = FontWeight.Bold)
//                    }
//                }
//            }
//        }
//    }
//}

//@Composable
//private fun InputFieldLabel(text: String) {
//    Text(
//        text = text,
//        color = AuraColors.TextSecondary,
//        fontSize = 12.sp,
//        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
//    )
//}

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
                Text("Tổng thời gian", color = AuraColors.TextSecondary, fontSize = 12.sp)
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
private fun SessionItem(session: LearningSessionRecord) {
    val statusColor = when (session.status) {
        LearningSessionStatus.COMPLETED -> AuraColors.SessionCompletedMode
        LearningSessionStatus.RUNNING -> AuraColors.WorkMode
        LearningSessionStatus.PAUSED -> AuraColors.SessionPausedMode
        LearningSessionStatus.IDLE -> AuraColors.SessionIdleMode
    }

    GlassBox(
        modifier = Modifier.fillMaxWidth(),
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
                    text = when (session.currentLearningMode) {
                        CurrentLearningMode.NOT_YET_STARTED -> "Chưa bắt đầu"
                        CurrentLearningMode.WORK -> "Đang làm việc"
                        CurrentLearningMode.BREAK -> "Nghỉ ngắn"
                        CurrentLearningMode.LONG_BREAK -> "Nghỉ dài"
                    },
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
                    text = session.status.toString().uppercase(),
                    color = statusColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}