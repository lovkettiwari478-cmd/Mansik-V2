package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.database.TaskEntity
import com.example.ui.theme.ManiskAmber
import com.example.ui.theme.ManiskCrimson
import com.example.ui.theme.ManiskCyan
import com.example.ui.theme.ManiskDarkBg
import com.example.ui.theme.ManiskEmerald
import com.example.ui.theme.ManiskSurface
import com.example.ui.theme.ManiskSurfaceBorder
import com.example.ui.theme.ManiskSurfaceElevated
import com.example.ui.theme.ManiskTextMuted
import com.example.ui.theme.ManiskTextPrimary
import com.example.ui.theme.ManiskTextSecondary
import com.example.ui.theme.ManiskViolet

@Composable
fun TasksScreen(
    tasks: List<TaskEntity>,
    onToggleTask: (TaskEntity) -> Unit,
    onAddTask: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var filterMode by remember { mutableStateOf("ALL") } // "ALL", "PENDING", "COMPLETED"

    var newTitle by remember { mutableStateOf("") }
    var newProject by remember { mutableStateOf("General") }
    var newPriority by remember { mutableStateOf("HIGH") }

    val filteredTasks = when (filterMode) {
        "PENDING" -> tasks.filter { it.status != "COMPLETED" }
        "COMPLETED" -> tasks.filter { it.status == "COMPLETED" }
        else -> tasks
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ManiskDarkBg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TASK GRAPH & SCHEDULE",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = ManiskCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Decomposed goals with verified completion criteria",
                            fontSize = 12.sp,
                            color = ManiskTextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Filter Pills
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ALL", "PENDING", "COMPLETED").forEach { mode ->
                        val isSelected = filterMode == mode
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ManiskCyan else ManiskSurface)
                                .border(1.dp, if (isSelected) ManiskCyan else ManiskSurfaceBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = mode,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color(0xFF042F2E) else ManiskTextSecondary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Tasks List
            items(filteredTasks) { task ->
                val isDone = task.status == "COMPLETED"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    colors = CardDefaults.cardColors(containerColor = ManiskSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, ManiskSurfaceBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onToggleTask(task) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.TaskAlt,
                                contentDescription = "Toggle completion",
                                tint = if (isDone) ManiskEmerald else ManiskCyan
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDone) ManiskTextMuted else ManiskTextPrimary
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "[${task.project}]",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = ManiskViolet
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Est: ${task.estimatedDuration}",
                                    fontSize = 11.sp,
                                    color = ManiskTextSecondary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    when (task.priority) {
                                        "URGENT" -> ManiskCrimson.copy(alpha = 0.2f)
                                        "HIGH" -> ManiskAmber.copy(alpha = 0.2f)
                                        else -> ManiskEmerald.copy(alpha = 0.2f)
                                    }
                                )
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = task.priority,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = when (task.priority) {
                                    "URGENT" -> ManiskCrimson
                                    "HIGH" -> ManiskAmber
                                    else -> ManiskEmerald
                                }
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(70.dp)) }
        }

        // FAB to add task
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = ManiskCyan,
            contentColor = Color(0xFF042F2E),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_task_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Task")
        }

        // Add Task Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                containerColor = ManiskSurface,
                title = {
                    Text(
                        text = "NEW SCHEDULED TASK",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ManiskCyan
                    )
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            label = { Text("Task Title") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ManiskCyan,
                                unfocusedBorderColor = ManiskSurfaceBorder,
                                focusedTextColor = ManiskTextPrimary,
                                unfocusedTextColor = ManiskTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = newProject,
                            onValueChange = { newProject = it },
                            label = { Text("Project / Category") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ManiskCyan,
                                unfocusedBorderColor = ManiskSurfaceBorder,
                                focusedTextColor = ManiskTextPrimary,
                                unfocusedTextColor = ManiskTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = newPriority,
                            onValueChange = { newPriority = it },
                            label = { Text("Priority (URGENT, HIGH, MEDIUM, LOW)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ManiskCyan,
                                unfocusedBorderColor = ManiskSurfaceBorder,
                                focusedTextColor = ManiskTextPrimary,
                                unfocusedTextColor = ManiskTextPrimary
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newTitle.isNotBlank()) {
                                onAddTask(newTitle, newPriority, newProject)
                                newTitle = ""
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ManiskCyan)
                    ) {
                        Text("SCHEDULE", color = Color(0xFF042F2E), fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("CANCEL", color = ManiskTextSecondary)
                    }
                }
            )
        }
    }
}
