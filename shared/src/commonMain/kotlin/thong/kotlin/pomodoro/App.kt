package thong.kotlin.pomodoro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.SlideTransition
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import thong.kotlin.pomodoro.core.media.SoundManager
import thong.kotlin.pomodoro.core.notification.NotificationManager

class LoadingScreen : Screen {
    @Composable
    override fun Content() {
        // Lấy navigator ra để chuyển màn hình
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            delay(2000)
            navigator.replace(SetupScreen())
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Đang khởi tạo...", fontSize = 18.sp, color = Color.Gray)
            }
        }
    }
}

class SetupScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        // Sử dụng State nội bộ (chỉ ở tầng UI) để lưu con số đang chọn tạm thời
        var chosenCount by remember { mutableStateOf(10) }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Chọn số bắt đầu", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(24.dp))

            // Bộ tăng giảm số đơn giản
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { if (chosenCount > 0) chosenCount-- }) {
                    Text("-", fontSize = 20.sp)
                }
                Text(
                    text = chosenCount.toString(),
                    fontSize = 36.sp,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                Button(onClick = { chosenCount++ }) {
                    Text("+", fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(onClick = {
                navigator.push(CounterScreen(chosenCount))
            }) {
                Text("Xác nhận & Bắt đầu", fontSize = 18.sp)
            }
        }
    }
}

class CounterScreen(private val startCount: Int) : Screen {

    @Composable
    override fun Content() {
        val counterViewModel = rememberScreenModel { CounterViewModel(startCount) }
        val currentCount by counterViewModel.count.collectAsState()

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isLandscape = maxWidth > maxHeight

            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Chế độ: Ngang", color = Color.Gray)
                    Text(text = "Số lần bấm: $currentCount", fontSize = 28.sp)
                    Button(onClick = { counterViewModel.increment() }) { Text("Tăng (+1)") }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Chế độ: Dọc", color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Số lần bấm: $currentCount", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { counterViewModel.increment() }) { Text("Tăng (+1)") }
                }
            }
        }
    }
}

@Composable
@Preview
fun App(
    soundManager: SoundManager? = null,
    notificationManager: NotificationManager? = null
) {
    MaterialTheme {
        // Khởi tạo Navigator với màn hình đầu tiên (Start Destination) là LoadingScreen
        Navigator(screen = LoadingScreen()) { navigator ->
            // Sử dụng SlideTransition để có hiệu ứng vuốt ngang mượt mà khi chuyển đổi giữa các màn hình
            SlideTransition(navigator = navigator)
        }
    }
}

class CounterViewModel(initialCount: Int) : ScreenModel {
    private val _count = MutableStateFlow(initialCount)

    val count: StateFlow<Int> = _count.asStateFlow()

    fun increment() {
        _count.value += 1
    }
}