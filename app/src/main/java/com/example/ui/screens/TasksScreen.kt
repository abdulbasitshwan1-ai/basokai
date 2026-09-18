package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskEntity
import com.example.data.model.TaskType
import com.example.ui.BasokaViewModel
import com.example.ui.theme.BasokaBlack
import com.example.ui.theme.BasokaPrimary
import com.example.ui.theme.BasokaSecondary
import com.example.ui.theme.BasokaSurface
import com.example.ui.theme.BasokaSurfaceBorder
import com.example.ui.theme.BasokaSurfaceElevated
import com.example.ui.theme.BasokaTertiary
import com.example.ui.theme.BasokaTextPrimary
import com.example.ui.theme.BasokaTextSecondary
import com.example.ui.theme.BasokaTextTertiary

@Composable
fun TasksScreen(
    viewModel: BasokaViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.allTasks.collectAsState()
    var selectedFilter by remember { mutableStateOf<TaskType?>(null) }

    val filterOptions = listOf(
        Pair("هەمووی", null),
        Pair("تایمەر", TaskType.TIMER),
        Pair("بیرخستنەوە", TaskType.REMINDER),
        Pair("زەنگ", TaskType.ALARM),
        Pair("ڕۆژژمێر", TaskType.CALENDAR),
        Pair("ئۆتۆماتیک", TaskType.AUTOMATION)
    )

    val filteredTasks = remember(tasks, selectedFilter) {
        if (selectedFilter == null) tasks else tasks.filter { it.taskType == selectedFilter }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BasokaBlack)
            .padding(horizontal = 16.dp)
            .testTag("tasks_screen")
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Header Title
        Text(
            text = "ئەرک و کارە چالاکەکان",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = BasokaTextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal Category Filter Pills
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filterOptions) { (label, type) ->
                val isSelected = selectedFilter == type
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { selectedFilter = type },
                    color = if (isSelected) BasokaPrimary else BasokaSurfaceElevated,
                    border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, BasokaSurfaceBorder) else null
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        color = if (isSelected) Color.White else BasokaTextSecondary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredTasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = BasokaTextTertiary,
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "هیچ کارێک لەم بەشەدا نییە",
                        fontSize = 15.sp,
                        color = BasokaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "لە ڕێگەی چاتەوە بڵێ: «تایمەرێکم بۆ دانێ» یان «بیرم بخەرەوە»",
                        fontSize = 12.sp,
                        color = BasokaTextTertiary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredTasks, key = { it.id }) { task ->
                    when (task.taskType) {
                        TaskType.TIMER -> TimerTaskCard(
                            task = task,
                            onTogglePlay = { viewModel.toggleTimer(task) },
                            onReset = { viewModel.resetTimer(task) },
                            onDelete = { viewModel.deleteTask(task.id) }
                        )
                        else -> StandardTaskCard(
                            task = task,
                            onToggleCompleted = { viewModel.toggleTaskComplete(task.id, !task.isCompleted) },
                            onToggleEnabled = { viewModel.toggleTaskEnabled(task.id, !task.isEnabled) },
                            onDelete = { viewModel.deleteTask(task.id) }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

@Composable
fun TimerTaskCard(
    task: TaskEntity,
    onTogglePlay: () -> Unit,
    onReset: () -> Unit,
    onDelete: () -> Unit
) {
    val minutes = task.timerRemainingSeconds / 60
    val seconds = task.timerRemainingSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("timer_card_${task.id}"),
        color = BasokaSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, BasokaPrimary.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(BasokaPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = BasokaPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = BasokaTextPrimary
                    )
                    Text(
                        text = formattedTime,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (task.isTimerRunning) BasokaTertiary else BasokaTextSecondary,
                        letterSpacing = 1.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onTogglePlay,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (task.isTimerRunning) BasokaSecondary else BasokaPrimary)
                ) {
                    Icon(
                        imageVector = if (task.isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (task.isTimerRunning) "وەستان" else "دەستپێکردن",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(onClick = onReset) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "ڕیسێت",
                        tint = BasokaTextSecondary
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "سڕینەوە",
                        tint = BasokaTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun StandardTaskCard(
    task: TaskEntity,
    onToggleCompleted: () -> Unit,
    onToggleEnabled: () -> Unit,
    onDelete: () -> Unit
) {
    val icon = when (task.taskType) {
        TaskType.REMINDER -> Icons.Default.Notifications
        TaskType.ALARM -> Icons.Default.Alarm
        TaskType.CALENDAR -> Icons.Default.CalendarMonth
        TaskType.AUTOMATION -> Icons.Default.SmartToy
        else -> Icons.Default.Notifications
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .testTag("task_card_${task.id}"),
        color = BasokaSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BasokaSurfaceBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (task.taskType == TaskType.REMINDER) {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = { onToggleCompleted() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = BasokaPrimary,
                            uncheckedColor = BasokaTextTertiary
                        )
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(BasokaSurfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = BasokaPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Column {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = if (task.isCompleted) BasokaTextTertiary else BasokaTextPrimary,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (task.timeLabel.isNotEmpty()) {
                            Text(
                                text = task.timeLabel,
                                fontSize = 12.sp,
                                color = BasokaSecondary
                            )
                        }
                        if (task.recurrence != "تاک") {
                            Text(
                                text = " • ${task.recurrence}",
                                fontSize = 11.sp,
                                color = BasokaTertiary
                            )
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (task.taskType == TaskType.ALARM || task.taskType == TaskType.AUTOMATION) {
                    Switch(
                        checked = task.isEnabled,
                        onCheckedChange = { onToggleEnabled() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BasokaPrimary,
                            uncheckedTrackColor = BasokaSurfaceBorder
                        ),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "سڕینەوە",
                        tint = BasokaTextSecondary
                    )
                }
            }
        }
    }
}
