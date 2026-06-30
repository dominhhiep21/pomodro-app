package thong.kotlin.pomodoro.features.learning.mode.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pomodrokotlin.shared.generated.resources.Res
import pomodrokotlin.shared.generated.resources.landspace_startup_bg
import thong.kotlin.pomodoro.core.config.AppConfig
import thong.kotlin.pomodoro.core.designsystem.components.AuraBackground
import thong.kotlin.pomodoro.core.designsystem.components.AuraButton
import thong.kotlin.pomodoro.core.designsystem.components.AuraInputField
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.di.DependencyRegistry
import thong.kotlin.pomodoro.features.learning.mode.domain.LearningGroupConfig
import thong.kotlin.pomodoro.features.learning.mode.domain.LearningStyle
import thong.kotlin.pomodoro.features.pomodoro._base.PomodoroScreenV2
import thong.kotlin.pomodoro.features.session.data.LearningSessionManager
import thong.kotlin.pomodoro.features.session.domain.CurrentLearningMode
import thong.kotlin.pomodoro.features.session.domain.LearningSessionEvent
import thong.kotlin.pomodoro.features.session.domain.LearningSessionEventType
import thong.kotlin.pomodoro.features.session.domain.LearningSessionRecord
import thong.kotlin.pomodoro.features.session.domain.LearningSessionStatus
import thong.kotlin.pomodoro.features.session.domain.SyncStatus
import thong.kotlin.pomodoro.features.session.presentation.SessionHistoryScreen
import kotlin.time.Clock

class LearningStyleScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val learningSessionManager = remember { DependencyRegistry.learningSessionManager }

        val scope = rememberCoroutineScope()
        var isFinishing by remember { mutableStateOf(false) }

        LearningStyleScreenUI(
            isLoading = isFinishing,
            onBack = {
                if (!isFinishing) {
                    navigator.replace(SessionHistoryScreen())
                }
            },
            onFinish = { learningStyle, learningGroupConfig ->
                if (isFinishing) return@LearningStyleScreenUI
                isFinishing = true
                scope.launch {
                    val result = runCatching {
                        val newSession = addNewSessionToDbAndGet(
                            learningGroupConfig = learningGroupConfig,
                            learningStyle = learningStyle,
                            learningSessionManager = learningSessionManager
                        )
                        addNewEventToDb(
                            sessionId = newSession.sessionId,
                            learningSessionManager = learningSessionManager
                        )
                        newSession
                    }
                    result
                        .onSuccess { newSession ->
                            navigator.push(
                                PomodoroScreenV2(
                                    learningStyle = learningStyle,
                                    learningGroupConfig = learningGroupConfig,
                                    currentSession = newSession
                                )
                            )
                        }
                        .onFailure { _ ->
                            isFinishing = false
                        }
                }
            }
        )
    }
}

private suspend fun addNewSessionToDbAndGet(
    learningGroupConfig: LearningGroupConfig?,
    learningStyle: LearningStyle,
    learningSessionManager: LearningSessionManager
): LearningSessionRecord {
    val now = Clock.System.now().toEpochMilliseconds()
    val workMin = learningGroupConfig?.workMinutes ?: AppConfig.DEFAULT_WORK_MINUTES
    val breakMin = learningGroupConfig?.breakMinutes ?: AppConfig.DEFAULT_BREAK_MINUTES
    val newSession = LearningSessionRecord(
        sessionId = "manual_$now",
        userId = null,
        anonymousUserId = null,
        sessionMode = learningStyle,
        status = LearningSessionStatus.IDLE,
        currentLearningMode = CurrentLearningMode.NOT_YET_STARTED,
        startedAtMillis = now,
        lastPausedAtMillis = null,
        plannedWorkMinutes = workMin,
        plannedBreakMinutes = breakMin,
        endedAtMillis = null,
        totalFocusSeconds = 0,
        syncStatus = SyncStatus.LOCAL_ONLY
    )
    learningSessionManager.insertSession(newSession)
    return newSession
}

private suspend fun addNewEventToDb(
    sessionId: String,
    learningSessionManager: LearningSessionManager
) {
    val newEvent = LearningSessionEvent(
        sessionId = sessionId,
        eventType = LearningSessionEventType.SESSION_CREATED
    )
    learningSessionManager.insertEvent(newEvent)
}

@Composable
private fun LearningStyleScreenUI(
    isLoading: Boolean,
    onBack: () -> Unit,
    onFinish: (LearningStyle, LearningGroupConfig?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showGroupSettingsPopup by remember { mutableStateOf(false) }

    // Group Settings State
    var selectedStyle by rememberSaveable { mutableStateOf(LearningStyle.SOLO) }
    var maxPeople by rememberSaveable { mutableStateOf("4") }
    var workMinutes by rememberSaveable { mutableStateOf("25") }
    var breakMinutes by rememberSaveable { mutableStateOf("5") }

    AuraBackground(
        blurRadius = 8f,
        overlayAlpha = 0.6f,
        landscapeImageRes = Res.drawable.landspace_startup_bg,
    ) {
        BoxWithConstraints(modifier = modifier.fillMaxSize()) {
            val isLandscape = maxWidth > maxHeight

            // Back Button
            Box(
                modifier = Modifier
                    .padding(
                        top = if (isLandscape) 12.dp else 24.dp,
                        start = if (isLandscape) 12.dp else 24.dp
                    )
                    .align(Alignment.TopStart)
            ) {
                GlassBox(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { onBack() },
                    shape = CircleShape,
                    backgroundColor = Color.White.copy(alpha = 0.1f)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 40.dp, bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Chọn phong cách học tập",
                        color = AuraColors.TextPrimary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Aura sẽ điều chỉnh không gian phù hợp với bạn",
                        color = AuraColors.TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }

                // Selection Cards
                if (isLandscape) {
                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(
                            24.dp,
                            Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SelectionCard(
                            title = "Cá nhân",
                            description = "Tập trung sâu một mình",
                            emoji = "🧘",
                            isSelected = selectedStyle == LearningStyle.SOLO,
                            onClick = { selectedStyle = LearningStyle.SOLO },
                            modifier = Modifier.weight(1f)
                        )
                        SelectionCard(
                            title = "Nhóm",
                            description = "Học cùng bạn bè khắp nơi",
                            emoji = "👥",
                            isSelected = selectedStyle == LearningStyle.GROUP,
                            onClick = { selectedStyle = LearningStyle.GROUP },
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(
                            16.dp,
                            Alignment.CenterVertically
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SelectionCard(
                            title = "Học tập Cá nhân",
                            description = "Không gian yên tĩnh để bạn tập trung tối đa",
                            emoji = "🧘",
                            isSelected = selectedStyle == LearningStyle.SOLO,
                            onClick = { selectedStyle = LearningStyle.SOLO },
                            modifier = Modifier.fillMaxWidth()
                        )
                        SelectionCard(
                            title = "Học tập theo Nhóm",
                            description = "Tham gia phòng học ảo và cùng nhau tiến bộ",
                            emoji = "👥",
                            isSelected = selectedStyle == LearningStyle.GROUP,
                            onClick = { selectedStyle = LearningStyle.GROUP },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Button
                AuraButton(
                    onClick = {
                        if (selectedStyle == LearningStyle.GROUP) {
                            showGroupSettingsPopup = true
                        } else {
                            onFinish(selectedStyle, null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Text(
                            text = "Tiếp tục",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // Group Settings Dialog
            if (showGroupSettingsPopup) {
                GroupSettingsDialog(
                    maxPeople = maxPeople,
                    workMinutes = workMinutes,
                    breakMinutes = breakMinutes,
                    onMaxPeopleChange = {
                        if (it.length <= 2) maxPeople = it.filter { char -> char.isDigit() }
                    },
                    onWorkMinutesChange = {
                        if (it.length <= 3) workMinutes = it.filter { char -> char.isDigit() }
                    },
                    onBreakMinutesChange = {
                        if (it.length <= 2) breakMinutes = it.filter { char -> char.isDigit() }
                    },
                    onDismiss = { showGroupSettingsPopup = false },
                    onConfirm = confirm@{
                        val maxPeopleValue = maxPeople.toIntOrNull()
                        val workMinutesValue = workMinutes.toIntOrNull()
                        val breakMinutesValue = breakMinutes.toIntOrNull()

                        if (
                            maxPeopleValue == null || maxPeopleValue <= 0 ||
                            workMinutesValue == null || workMinutesValue <= 0 ||
                            breakMinutesValue == null || breakMinutesValue <= 0
                        ) {
                            return@confirm
                        }

                        showGroupSettingsPopup = false

                        onFinish(
                            LearningStyle.GROUP,
                            LearningGroupConfig(
                                maxPeopleValue,
                                workMinutesValue,
                                breakMinutesValue
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun GroupSettingsDialog(
    maxPeople: String,
    workMinutes: String,
    breakMinutes: String,
    onMaxPeopleChange: (String) -> Unit,
    onWorkMinutesChange: (String) -> Unit,
    onBreakMinutesChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        GlassBox(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(28.dp),
            backgroundColor = Color.Black.copy(alpha = 0.8f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Thiết lập nhóm của bạn",
                    color = AuraColors.TextPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Cài đặt thời gian và số lượng thành viên cho phòng học nhóm",
                    color = AuraColors.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GroupSettingInput(
                        label = "Số người",
                        value = maxPeople,
                        onValueChange = onMaxPeopleChange,
                        modifier = Modifier.weight(1f)
                    )
                    GroupSettingInput(
                        label = "Phút học",
                        value = workMinutes,
                        onValueChange = onWorkMinutesChange,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                GroupSettingInput(
                    label = "Phút nghỉ",
                    value = breakMinutes,
                    onValueChange = onBreakMinutesChange,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AuraButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Hủy", color = AuraColors.TextSecondary, fontSize = 14.sp)
                    }

                    AuraButton(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1.5f)
                    ) {
                        Text(
                            text = "Xác nhận",
                            color = AuraColors.WorkMode,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupSettingInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            color = AuraColors.TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        AuraInputField(
            value = value,
            onValueChange = onValueChange,
            placeholder = "",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SelectionCard(
    title: String,
    description: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor =
        if (isSelected) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f)

    GlassBox(
        modifier = modifier
            .height(180.dp)
            .clickable { onClick() },
        backgroundColor = backgroundColor,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 32.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                color = AuraColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                color = AuraColors.TextSecondary,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }

        // Selection Indicator
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(AuraColors.WorkMode, CircleShape)
                        .padding(4.dp)
                ) {
                    // Could add a checkmark icon here
                }
            }
        }
    }
}
