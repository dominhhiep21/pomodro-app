package thong.kotlin.pomodoro.features.settings.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.features.pomodoro.viewmodel.TotallyPomodoroUiState

@Composable
fun SettingsUiComponent(
    totallyPomodoroUiState: TotallyPomodoroUiState,
    onWorkChange: (String) -> Unit,
    onBreakChange: (String) -> Unit,
    onSave: () -> Unit,
    onReset: () -> Unit,
    onHardReset: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Cấu hình Timer",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Input Fields Row/Column
        BoxWithConstraints {
            val isWide = maxWidth > 300.dp
            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DurationInput(
                        label = "Tập trung",
                        value = totallyPomodoroUiState.workspaceUiState.editingWorkMinutes,
                        onValueChange = onWorkChange,
                        modifier = Modifier.weight(1f)
                    )
                    DurationInput(
                        label = "Nghỉ ngơi",
                        value = totallyPomodoroUiState.workspaceUiState.editingBreakMinutes,
                        onValueChange = onBreakChange,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DurationInput(
                        label = "Tập trung",
                        value = totallyPomodoroUiState.workspaceUiState.editingWorkMinutes,
                        onValueChange = onWorkChange
                    )
                    DurationInput(
                        label = "Nghỉ ngơi",
                        value = totallyPomodoroUiState.workspaceUiState.editingBreakMinutes,
                        onValueChange = onBreakChange
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = totallyPomodoroUiState.workspaceUiState.settingsError,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(
                onClick = onReset,
                modifier = Modifier.weight(1f)
            ) {
                Text("Mặc định", color = Color.White.copy(alpha = 0.6f))
            }

            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AuraColors.WorkMode
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Lưu", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hard Reset Button
        TextButton(
            onClick = onHardReset,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
        ) {
            Text("Xóa tất cả dữ liệu & Cài đặt", fontSize = 12.sp)
        }
    }
}

@Composable
private fun DurationInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = AuraColors.WorkMode,
                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                cursorColor = AuraColors.WorkMode
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            suffix = { Text("phút", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp) }
        )
    }
}