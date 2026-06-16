package thong.kotlin.pomodoro.features.onboarding.presentation

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import pomodrokotlin.shared.generated.resources.Res
import pomodrokotlin.shared.generated.resources.landspace_startup_bg
import thong.kotlin.pomodoro.core.designsystem.components.AuraBackground
import thong.kotlin.pomodoro.core.designsystem.components.AuraButton
import thong.kotlin.pomodoro.core.designsystem.components.AuraInputField
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.features.pomodoro.domain.model.LearningStyle

@Composable
fun LearningStyleScreen(
    onSelectionComplete: (LearningStyle, Int, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedStyle by remember { mutableStateOf(LearningStyle.SOLO) }
    var showGroupSettingsPopup by remember { mutableStateOf(false) }
    
    // Group Settings State
    var maxPeople by remember { mutableStateOf("4") }
    var workMinutes by remember { mutableStateOf("25") }
    var breakMinutes by remember { mutableStateOf("5") }

    AuraBackground(
        blurRadius = 8f,
        overlayAlpha = 0.6f,
        landscapeImageRes = Res.drawable.landspace_startup_bg,
    ) {
        BoxWithConstraints(modifier = modifier.fillMaxSize()) {
            val isLandscape = maxWidth > maxHeight

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
                        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
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
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
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
                            onSelectionComplete(
                                selectedStyle, 
                                1, 
                                workMinutes.toIntOrNull() ?: 25,
                                breakMinutes.toIntOrNull() ?: 5
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Tiếp tục",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            // Group Settings Dialog
            if (showGroupSettingsPopup) {
                GroupSettingsDialog(
                    maxPeople = maxPeople,
                    workMinutes = workMinutes,
                    breakMinutes = breakMinutes,
                    onMaxPeopleChange = { if (it.length <= 2) maxPeople = it.filter { char -> char.isDigit() } },
                    onWorkMinutesChange = { if (it.length <= 3) workMinutes = it.filter { char -> char.isDigit() } },
                    onBreakMinutesChange = { if (it.length <= 2) breakMinutes = it.filter { char -> char.isDigit() } },
                    onDismiss = { showGroupSettingsPopup = false },
                    onConfirm = {
                        showGroupSettingsPopup = false
                        onSelectionComplete(
                            LearningStyle.GROUP,
                            maxPeople.toIntOrNull() ?: 4,
                            workMinutes.toIntOrNull() ?: 25,
                            breakMinutes.toIntOrNull() ?: 5
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
    val backgroundColor = if (isSelected) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f)

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
