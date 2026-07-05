package thong.kotlin.pomodoro.features.pomodoro.viewmodel

import thong.kotlin.pomodoro.features.pomodoro.task.domain.model.SessionTask

data class TasksUiState(
    val sessionTasks: List<SessionTask> = emptyList(),
    val newTaskText: String = "",
    val isTasksExpanded: Boolean = false,
    val taskValidationError: String? = null,
    val willBeInProgressTaskId: String? = null,
    val nextProgressTaskPosition: Int = -1
)