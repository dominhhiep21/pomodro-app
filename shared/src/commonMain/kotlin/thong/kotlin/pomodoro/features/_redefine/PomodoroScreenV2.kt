package thong.kotlin.pomodoro.features._redefine

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen

class PomodoroScreenV2() : Screen {

    @Composable
    override fun Content() {
        val timerViewModel: TimerViewModel = viewModel { TimerViewModel() }
        val tasksViewModel: TasksViewModel = viewModel { TasksViewModel() }
        val workspaceViewModel: WorkspaceViewModel = viewModel { WorkspaceViewModel() }

        PomodoroScreenUIv2(timerViewModel, tasksViewModel, workspaceViewModel)
    }
}

@Composable
fun PomodoroScreenUIv2(
    timerViewModel: TimerViewModel,
    tasksViewModel: TasksViewModel,
    workspaceViewModel: WorkspaceViewModel
) {


}