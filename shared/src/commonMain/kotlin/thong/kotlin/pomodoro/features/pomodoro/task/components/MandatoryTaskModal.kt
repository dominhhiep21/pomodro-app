package thong.kotlin.pomodoro.features.pomodoro.task.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import thong.kotlin.pomodoro.core.designsystem.components.AuraButton
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.features.pomodoro.viewmodel.TotallyPomodoroUiState

@Composable
fun MandatoryTaskModal(
    totallyPomodoroUiState: TotallyPomodoroUiState,
    onAddTask: () -> Unit,
    onDeleteTask: (String) -> Unit,
    onToggleTask: (String) -> Unit,
    onNewTaskTextChange: (String) -> Unit,
    onStartFocus: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        GlassBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            backgroundColor = Color.Black.copy(alpha = 0.95f)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Warning Icon Header
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = AuraColors.WorkMode,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Chưa có công việc!",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Bạn cần ít nhất một mục tiêu để bắt đầu phiên tập trung này.",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Reusing TaskSection for input
                TaskSection(
                    sessionTasks = totallyPomodoroUiState.tasksUiState.sessionTasks,
                    newTaskText = totallyPomodoroUiState.tasksUiState.newTaskText,
                    onAddTask = onAddTask,
                    onDeleteTask = onDeleteTask,
                    onToggleTask = onToggleTask,
                    onNewTaskTextChange = onNewTaskTextChange,
                    useLazyColumn = true,
                    validationError = totallyPomodoroUiState.tasksUiState.taskValidationError,
                    isReadOnly = false,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Spacer(modifier = Modifier.height(24.dp))

                val hasTasks = totallyPomodoroUiState.tasksUiState.sessionTasks.any { !it.isCompleted }

                AnimatedContent(
                    targetState = hasTasks,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "FooterTransition"
                ) { tasksPresent ->
                    if (tasksPresent) {
                        AuraButton(
                            onClick = onStartFocus,
                            horizontalPadding = 32.dp,
                            verticalPadding = 12.dp
                        ) {
                            Text(
                                "BẮT ĐẦU TẬP TRUNG",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    } else {
                        TextButton(
                            onClick = onDismiss
                        ) {
                            Text(
                                "Tôi sẽ bổ sung sau",
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
