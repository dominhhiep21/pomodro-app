package thong.kotlin.pomodoro.features.pomodoro.viewmodel

import thong.kotlin.pomodoro.features.pomodoro.task.domain.model.Task

data class TasksUiState(
    val tasks: List<Task> = emptyList(),
    val newTaskText: String = "",
    val isTasksExpanded: Boolean = false
)