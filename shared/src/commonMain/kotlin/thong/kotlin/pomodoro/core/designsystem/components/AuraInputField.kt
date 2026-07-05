package thong.kotlin.pomodoro.core.designsystem.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors

@Composable
fun AuraInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    enabled: Boolean = true
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        modifier = modifier.onPreviewKeyEvent {
            if (it.key == Key.Spacebar) {
                // On Desktop, prevent the space key from bubbling up to parent containers 
                // (like GlassBox with .clickable) which would trigger a toggle event.
                true
            } else {
                false
            }
        },
        textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
        placeholder = {
            Text(text = placeholder, color = AuraColors.TextSecondary, fontSize = 15.sp)
        },
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        // Tùy chỉnh màu sắc để biến TextField thành dạng trong suốt hòa vào nền
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = 0.08f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.04f),
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,   // Ẩn đường gạch chân khi click vào
            unfocusedIndicatorColor = Color.Transparent, // Ẩn đường gạch chân khi bình thường
            cursorColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    )
}