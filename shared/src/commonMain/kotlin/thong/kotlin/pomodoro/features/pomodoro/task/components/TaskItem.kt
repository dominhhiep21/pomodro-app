package thong.kotlin.pomodoro.features.pomodoro.task.components

import thong.kotlin.pomodoro.features.pomodoro.task.domain.model.SessionTask
import thong.kotlin.pomodoro.features.pomodoro.task.domain.model.TaskStatus

import androidx.compose.animation.core.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import thong.kotlin.pomodoro.core.designsystem.components.AuraCheckbox
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors

@Composable
fun TaskItem(
    sessionTask: SessionTask,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null,
    compact: Boolean = false,
    isOpenForDeleted : Boolean = true
) {
    GlassBox(
        shape = RoundedCornerShape(if (compact) 12.dp else 16.dp),
        animateColor = sessionTask.status == TaskStatus.IN_PROGRESS && !sessionTask.isCompleted,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (compact) 8.dp else 12.dp, vertical = if (compact) 6.dp else 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                AuraCheckbox(
                    checked = sessionTask.isCompleted,
                    onCheckedChange = { onToggle() },
                    activeColor = AuraColors.WorkMode,
                    size = if (compact) 20.dp else 24.dp
                )

                Spacer(modifier = Modifier.width(if (compact) 8.dp else 12.dp))

                val scrollState = rememberScrollState()
                var containerWidth by remember { mutableIntStateOf(0) }
                var textWidth by remember { mutableIntStateOf(0) }

                LaunchedEffect(textWidth, containerWidth) {
                    if (containerWidth in 1..<textWidth) {
                        while (true) {
                            delay(2000) // Pause at start
                            scrollState.animateScrollTo(
                                value = textWidth - containerWidth,
                                animationSpec = tween(
                                    durationMillis = ((textWidth - containerWidth) * 15).coerceAtLeast(1000),
                                    easing = LinearEasing
                                )
                            )
                            delay(2000) // Pause at end
                            scrollState.scrollTo(0)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .onSizeChanged { containerWidth = it.width }
                        .horizontalScroll(scrollState, enabled = false) // Disable manual scroll to let animation take over
                ) {
                    Column {
                        when (sessionTask.status) {
                            TaskStatus.IN_PROGRESS if !sessionTask.isCompleted -> {
                                Text(
                                    text = "ĐANG THỰC HIỆN",
                                    color = AuraColors.WorkMode,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                            TaskStatus.PAUSED if !sessionTask.isCompleted -> {
                                Text(
                                    text = "ĐANG TẠM DỪNG",
                                    color = AuraColors.BestStreakDay,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                            TaskStatus.COMPLETED -> {
                                Text(
                                    text = "ĐÃ HOÀN THÀNH",
                                    color = AuraColors.IncreaseMode,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }

                            else -> {}
                        }
                        Text(
                            text = sessionTask.title,
                            color = if (sessionTask.isCompleted) AuraColors.TextSecondary else Color.White,
                            fontSize = if (compact) 14.sp else 15.sp,
                            textDecoration = if (sessionTask.isCompleted) TextDecoration.LineThrough else null,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.onSizeChanged { textWidth = it.width }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(if (compact) 4.dp else 8.dp))

            if (!compact && onMoveUp != null && onMoveDown != null) {
                Column {
                    IconButton(
                        onClick = onMoveUp,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Move Up",
                            tint = AuraColors.TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onMoveDown,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Move Down",
                            tint = AuraColors.TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            IconButton(
                onClick = { if (isOpenForDeleted) onDelete() },
                modifier = Modifier.size(if (compact) 28.dp else 32.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = AuraColors.TextSecondary,
                    modifier = Modifier.size(if (compact) 18.dp else 20.dp)
                )
            }
        }
    }
}