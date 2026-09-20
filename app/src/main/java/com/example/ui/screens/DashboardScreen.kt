package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.WindPower
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.database.AuditLogEntity
import com.example.database.AutomationRuleEntity
import com.example.database.CalendarEventEntity
import com.example.database.SmartDeviceEntity
import com.example.database.TaskEntity
import com.example.ui.theme.ManiskAmber
import com.example.ui.theme.ManiskCrimson
import com.example.ui.theme.ManiskCyan
import com.example.ui.theme.ManiskDarkBg
import com.example.ui.theme.ManiskEmerald
import com.example.ui.theme.ManiskIndigo
import com.example.ui.theme.ManiskSurface
import com.example.ui.theme.ManiskSurfaceBorder
import com.example.ui.theme.ManiskSurfaceElevated
import com.example.ui.theme.ManiskTextMuted
import com.example.ui.theme.ManiskTextPrimary
import com.example.ui.theme.ManiskTextSecondary
import com.example.ui.theme.ManiskViolet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    tasks: List<TaskEntity>,
    events: List<CalendarEventEntity>,
    devices: List<SmartDeviceEntity>,
    automations: List<AutomationRuleEntity>,
    recentLogs: List<AuditLogEntity>,
    onToggleTask: (TaskEntity) -> Unit,
    onToggleDevice: (SmartDeviceEntity) -> Unit,
    onNavigateToTerminal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    val greeting = when {
        currentHour < 12 -> "Good morning"
        currentHour < 17 -> "Good afternoon"
        else -> "Good evening"
    }

    val pendingTasks = tasks.filter { it.status != "COMPLETED" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ManiskDarkBg)
            .padding(16.dp)
    ) {
        // Executive Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ManiskCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = ManiskSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "$greeting, Anita.",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = ManiskTextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "MANISK AI Operating System is active. You have ${pendingTasks.size} pending tasks, ${events.size} scheduled calendar events, and ${automations.filter { it.isEnabled }.size} active automation protocols.",
                        fontSize = 13.sp,
                        color = ManiskTextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ManiskSurfaceElevated)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = ManiskEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Zero-Trust Guard: All 13 Agents Verified",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = ManiskEmerald
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ManiskCyan.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "100% AUDITED",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                color = ManiskCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Quick OS Action Launch
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ManiskSurface)
                    .border(1.dp, ManiskSurfaceBorder, RoundedCornerShape(12.dp))
                    .clickable { onNavigateToTerminal() }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ManiskCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = ManiskCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Launch Orchestrator Loop",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = ManiskTextPrimary
                        )
                        Text(
                            text = "Understand • Plan • Permission Check • Verify",
                            fontSize = 11.sp,
                            color = ManiskTextSecondary
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ManiskCyan)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "TERMINAL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF042F2E)
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Section: Connected Smart Devices
        item {
            Text(
                text = "CONNECTED SMART APPLIANCES",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ManiskTextMuted,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(devices) { device ->
            val isOn = device.stateJson.contains("\"isOn\":true")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isOn) ManiskSurfaceElevated else ManiskSurface
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    0.5.dp,
                    if (isOn) ManiskCyan.copy(alpha = 0.5f) else ManiskSurfaceBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val icon = when (device.type) {
                            "AC" -> Icons.Default.AcUnit
                            "LIGHT" -> Icons.Default.Lightbulb
                            "FAN" -> Icons.Default.WindPower
                            "TV" -> Icons.Default.Tv
                            else -> Icons.Default.Thermostat
                        }
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isOn) ManiskCyan.copy(alpha = 0.2f) else ManiskSurfaceBorder),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isOn) ManiskCyan else ManiskTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = device.name,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = ManiskTextPrimary
                            )
                            Text(
                                text = "${device.room} • ${if (isOn) "ACTIVE" else "STANDBY"}",
                                fontSize = 11.sp,
                                color = if (isOn) ManiskEmerald else ManiskTextSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Switch(
                        checked = isOn,
                        onCheckedChange = { onToggleDevice(device) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ManiskCyan,
                            checkedTrackColor = ManiskCyan.copy(alpha = 0.3f),
                            uncheckedThumbColor = ManiskTextMuted,
                            uncheckedTrackColor = ManiskSurfaceBorder
                        ),
                        modifier = Modifier.testTag("device_toggle_${device.deviceId}")
                    )
                }
            }
        }

        // Section: Priorities & Upcoming Tasks
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PRIORITY TASKS",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ManiskTextMuted,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${pendingTasks.size} Pending",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = ManiskCyan
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(tasks.take(4)) { task ->
            val isCompleted = task.status == "COMPLETED"
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = ManiskSurface),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, ManiskSurfaceBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onToggleTask(task) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.TaskAlt,
                                contentDescription = "Toggle Task",
                                tint = if (isCompleted) ManiskEmerald else ManiskTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = task.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isCompleted) ManiskTextMuted else ManiskTextPrimary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "[${task.project}]",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = ManiskViolet
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Est: ${task.estimatedDuration}",
                                    fontSize = 10.sp,
                                    color = ManiskTextSecondary
                                )
                            }
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
                            .padding(horizontal = 6.dp, vertical = 2.dp)
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

        // Section: Upcoming Calendar Events
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "UPCOMING EVENTS",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ManiskTextMuted,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(events) { event ->
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            val timeString = "${timeFormat.format(Date(event.startTime))} - ${timeFormat.format(Date(event.endTime))}"
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = ManiskSurface),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, ManiskSurfaceBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(ManiskViolet.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = ManiskViolet,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = event.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ManiskTextPrimary
                            )
                            Text(
                                text = "$timeString • ${event.location}",
                                fontSize = 11.sp,
                                color = ManiskTextSecondary
                            )
                        }
                    }

                    if (event.isVerified) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Verified",
                                tint = ManiskEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Verified",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = ManiskEmerald
                            )
                        }
                    }
                }
            }
        }

        // Section: Recent Verified Actions (Audit Trail)
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "RECENT VERIFIED ACTIONS",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ManiskTextMuted,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(recentLogs.take(3)) { log ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                colors = CardDefaults.cardColors(containerColor = ManiskSurfaceElevated),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = log.action,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ManiskTextPrimary
                        )
                        Text(
                            text = log.status,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = if (log.status == "SUCCESS") ManiskEmerald else ManiskCrimson
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Agent: ${log.agent} • Evidence: ${log.verification}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = ManiskTextSecondary
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
