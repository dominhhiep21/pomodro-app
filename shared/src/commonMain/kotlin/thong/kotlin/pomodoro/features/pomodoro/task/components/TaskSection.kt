package thong.kotlin.pomodoro.features.pomodoro.task.components

import thong.kotlin.pomodoro.features.pomodoro.task.domain.model.SessionTask

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import thong.kotlin.pomodoro.core.designsystem.components.AuraButton
import thong.kotlin.pomodoro.core.designsystem.components.AuraInputField
import thong.kotlin.pomodoro.features.pomodoro._base.components.BreakEndBannerSmall

@Composable
fun TaskSection(
    modifier: Modifier = Modifier,
    sessionTasks: List<SessionTask>,
    newTaskText: String,
    onAddTask: () -> Unit,
    onDeleteTask: (String) -> Unit,
    onToggleTask: (String) -> Unit,
    onNewTaskTextChange: (String) -> Unit,
    showBreakEndBanner: Boolean = false,
    useLazyColumn: Boolean = true
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxWidth()) {
        AuraInputField(
            value = newTaskText,
            onValueChange = onNewTaskTextChange,
            placeholder = "Hôm nay bạn cần làm gì?",
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                AuraButton(
                    onClick = {
                        onAddTask()
                        focusManager.clearFocus()
                    }
                ) {
                    Text("+", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (showBreakEndBanner) {
            BreakEndBannerSmall()
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (useLazyColumn) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(sessionTasks, key = { it.taskId }) { task ->
                    val onToggle = remember(task.taskId, onToggleTask) { { onToggleTask(task.taskId) } }
                    val onDelete = remember(task.taskId, onDeleteTask) { { onDeleteTask(task.taskId) } }
                    
                    TaskItem(
                        sessionTask = task,
                        onToggle = onToggle,
                        onDelete = onDelete
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                sessionTasks.forEach { task ->
                    val onDelete = remember(task.taskId, onDeleteTask) { { onDeleteTask(task.taskId) } }
                    val onToggle = remember(task.taskId, onToggleTask) { { onToggleTask(task.taskId) } }

                    TaskItem(
                        sessionTask = task,
                        onToggle = onToggle,
                        onDelete = onDelete
                    )
                }
            }
        }
    }
}