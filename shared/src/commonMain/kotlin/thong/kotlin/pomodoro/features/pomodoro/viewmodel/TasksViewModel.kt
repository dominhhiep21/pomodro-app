package thong.kotlin.pomodoro.features.pomodoro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import thong.kotlin.pomodoro.core.utils.getCurrentDateTimeString
import thong.kotlin.pomodoro.features.pomodoro._base.domain.repository.UserAppStateRepository
import thong.kotlin.pomodoro.features.pomodoro.task.domain.model.Task
import kotlin.random.Random

data class TasksUiState(
    val tasks: List<Task> = emptyList(),
    val newTaskText: String = "",
    val isTasksExpanded: Boolean = false
)

class TasksViewModel(
    private val repository: UserAppStateRepository? = null
) : ViewModel() {
    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            repository?.getAllTasks()?.collect { tasks ->
                _uiState.update { it.copy(tasks = tasks) }
            }
        }
    }

    fun onNewTaskTextChange(text: String) {
        _uiState.update { it.copy(newTaskText = text) }
    }

    /**
     * Thêm task mới. Có thể truyền sessionId nếu task này được tạo ra
     * trong lúc bộ đếm Pomodoro đang chạy.
     */
    fun addTask(currentSessionId: String? = null) {
        val text = _uiState.value.newTaskText
        if (text.isNotBlank()) {
            // Tạo ID ngẫu nhiên (Trong thực tế KMP, bạn có thể dùng kotlinx-uuid hoặc Random)
            val taskId = Random.nextLong().toString()

            val newTask = Task(
                id = taskId,
                text = text,
                isCompleted = false,
                createdAt = getCurrentDateTimeString(), // Giả định bạn đã có hàm này
                completedAt = null,
                sessionId = currentSessionId
            )

            // Cập nhật UI ngay lập tức
            _uiState.update {
                it.copy(
                    tasks = it.tasks + newTask,
                    newTaskText = ""
                )
            }

            // Lưu xuống Database
            viewModelScope.launch {
                repository?.saveTask(newTask)
            }
        }
    }

    fun deleteTask(taskId: String) {
        _uiState.update { state ->
            state.copy(tasks = state.tasks.filter { it.id != taskId })
        }

        viewModelScope.launch {
            repository?.deleteTask(taskId)
        }
    }

    fun toggleTask(taskId: String) {
        var updatedTask: Task? = null

        _uiState.update { state ->
            state.copy(
                tasks = state.tasks.map { task ->
                    if (task.id == taskId) {
                        val newStatus = !task.isCompleted
                        updatedTask = task.copy(
                            isCompleted = newStatus,
                            completedAt = if (newStatus) getCurrentDateTimeString() else null
                        )
                        updatedTask
                    } else {
                        task
                    }
                }
            )
        }

        updatedTask?.let { task ->
            viewModelScope.launch {
                repository?.saveTask(task)
            }
        }
    }

    fun toggleTasksExpanded() {
        _uiState.update { it.copy(isTasksExpanded = !it.isTasksExpanded) }
    }
}