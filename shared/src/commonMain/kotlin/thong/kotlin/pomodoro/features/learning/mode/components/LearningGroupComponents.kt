package thong.kotlin.pomodoro.features.learning.mode.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thong.kotlin.pomodoro.core.designsystem.components.AuraHorizontalScrollbar
import thong.kotlin.pomodoro.core.designsystem.components.AuraInputField
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.features.learning.mode.domain.ChatMessage
import thong.kotlin.pomodoro.features.learning.mode.domain.ExpandDirection
import kotlin.math.roundToInt

@Composable
fun RoomIdBadge(roomId: String, modifier: Modifier = Modifier) {
    GlassBox(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color.White.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PHÒNG: ",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "#$roomId",
                color = AuraColors.WorkMode,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Suppress("FrequentlyChangingValue")
@Composable
fun ExpandableMembersPanelBubble(
    modifier: Modifier = Modifier,
    expandDirection: ExpandDirection = ExpandDirection.TO_RIGHT
) {
    var isExpanded by remember { mutableStateOf(false) }
    val participants = remember {
        listOf("Bạn", "Minh", "Lan", "Phong", "Trang", "Hoàng", "Nam", "An")
    }

    val panelAlignment = when (expandDirection) {
        ExpandDirection.TO_LEFT -> Alignment.TopEnd
        ExpandDirection.TO_RIGHT -> Alignment.TopStart
    }

    Box(
        modifier = modifier.animateContentSize(),
        contentAlignment = panelAlignment
    ) {
        if (!isExpanded) {
            // Bubble State
            Box(contentAlignment = Alignment.TopEnd) {
                GlassBox(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { isExpanded = true },
                    shape = CircleShape,
                    backgroundColor = AuraColors.BottomBarBackground
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = "Members",
                        tint = AuraColors.WorkMode,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Count Badge
                GlassBox(
                    shape = CircleShape,
                    backgroundColor = AuraColors.WorkMode,
                    modifier = Modifier.size(20.dp)
                ) {
                    Text(
                        text = participants.size.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            // Expanded Panel State
            GlassBox(
                modifier = Modifier
                    .width(300.dp),
                shape = RoundedCornerShape(24.dp),
                backgroundColor = AuraColors.BottomBarBackground
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { isExpanded = false }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Cụm Chữ + Số lượng
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Thành viên",
                                color = AuraColors.TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            GlassBox(
                                shape = CircleShape,
                                backgroundColor = AuraColors.WorkMode.copy(alpha = 0.2f),
                                modifier = Modifier.size(22.dp)
                            ) {
                                Text(
                                    text = participants.size.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Cụm Icon Collapse
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Collapse",
                            tint = AuraColors.TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    val lazyListState = rememberLazyListState()

                    Spacer(modifier = Modifier.height(12.dp))
                    // --- DANH SÁCH THÀNH VIÊN ---
                    LazyRow(
                        state = lazyListState,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(participants) { name ->
                            MemberAvatar(name = name)
                        }
                    }

                    AuraHorizontalScrollbar(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ExpandableMembersPanel(modifier: Modifier = Modifier) {
    var isExpanded by remember { mutableStateOf(false) }
    val participants = remember {
        listOf("Bạn", "Minh", "Lan", "Phong", "Trang", "Hoàng", "Nam", "An")
    }

    Column(
        modifier = modifier
            .animateContentSize()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .clickable { isExpanded = !isExpanded }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Thành viên",
                    color = AuraColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                GlassBox(
                    shape = CircleShape,
                    backgroundColor = AuraColors.WorkMode.copy(alpha = 0.2f),
                    modifier = Modifier.size(22.dp)
                ) {
                    Text(
                        text = participants.size.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand",
                tint = AuraColors.TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(participants) { name ->
                    MemberAvatar(name = name)
                }
            }
        } else {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = participants.joinToString(", "),
                color = AuraColors.TextSecondary,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun MemberAvatar(name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1),
                color = AuraColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            color = AuraColors.TextSecondary,
            fontSize = 9.sp
        )
    }
}

@Composable
fun ExpandableChatPanel(
    modifier: Modifier = Modifier,
    expandDirection: ExpandDirection = ExpandDirection.TO_LEFT,
    initialOffset: Offset = Offset.Zero
) {
    var isExpanded by remember { mutableStateOf(false) }
    var panelOffset by remember { mutableStateOf(initialOffset) }
    var messageText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ChatMessage("Minh", "Chào cả nhà!", isMe = false),
            ChatMessage("Lan", "Cố gắng học thôi nào!", isMe = false),
            ChatMessage("Hoàng", "Mọi người tập trung ghê quá :D", isMe = false)
        )
    }

    // Cài đặt điểm neo (anchor) dựa trên hướng muốn mở rộng.
    val panelAlignment = when (expandDirection) {
        ExpandDirection.TO_LEFT -> Alignment.BottomEnd   // Neo góc phải -> nở sang trái
        ExpandDirection.TO_RIGHT -> Alignment.BottomStart // Neo góc trái -> nở sang phải
    }

    Box(
        modifier = modifier
            .offset { IntOffset(panelOffset.x.roundToInt(), panelOffset.y.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    panelOffset = Offset(
                        x = panelOffset.x + dragAmount.x,
                        y = panelOffset.y + dragAmount.y
                    )
                }
            }
            .animateContentSize(),
        contentAlignment = panelAlignment
    ) {
        if (!isExpanded) {
            // Bubble State
            GlassBox(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { isExpanded = true },
                shape = CircleShape,
                backgroundColor = AuraColors.BottomBarBackground
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = "Open Chat",
                    tint = AuraColors.WorkMode,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            // Expanded Panel State
            GlassBox(
                modifier = Modifier
                    .width(320.dp)
                    .heightIn(max = 400.dp),
                shape = RoundedCornerShape(24.dp),
                backgroundColor = AuraColors.BottomBarBackground
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Trò chuyện",
                            color = AuraColors.TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(onClick = { isExpanded = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Collapse Chat",
                                tint = AuraColors.TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .heightIn(max = 250.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(messages) { msg ->
                            ChatBubble(msg = msg)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AuraInputField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            placeholder = "Nhắn tin...",
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    messages.add(ChatMessage("Bạn", messageText, true))
                                    messageText = ""
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AuraColors.WorkMode.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(msg: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (msg.isMe) Alignment.End else Alignment.Start
    ) {
        Text(
            text = msg.sender,
            color = AuraColors.TextSecondary,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        GlassBox(
            shape = RoundedCornerShape(12.dp),
            backgroundColor = if (msg.isMe) AuraColors.WorkMode.copy(alpha = 0.2f) else Color.White.copy(
                alpha = 0.05f
            ),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            Text(
                text = msg.text,
                color = Color.White,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}
