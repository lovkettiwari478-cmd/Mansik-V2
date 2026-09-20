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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.WindPower
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.database.AutomationRuleEntity
import com.example.database.SmartDeviceEntity
import com.example.ui.theme.ManiskAmber
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
fun SmartHomeScreen(
    devices: List<SmartDeviceEntity>,
    automations: List<AutomationRuleEntity>,
    onToggleDevice: (SmartDeviceEntity) -> Unit,
    onToggleAutomation: (AutomationRuleEntity) -> Unit,
    onTriggerAutomationMode: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ManiskDarkBg)
            .padding(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "SMART ENVIRONMENT & IOT SUBSYSTEM",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ManiskCyan,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Zero-trust appliance automation with bounded safety ranges",
                    fontSize = 12.sp,
                    color = ManiskTextSecondary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Section: Contextual Automation Protocols
        item {
            Text(
                text = "CONTEXTUAL AUTOMATION PROTOCOLS",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ManiskTextMuted,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(automations) { rule ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = ManiskSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, ManiskSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val icon = when (rule.mode) {
                                "WELCOME_HOME" -> Icons.Default.Bolt
                                "MEETING" -> Icons.Default.MeetingRoom
                                else -> Icons.Default.NightlightRound
                            }
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(ManiskViolet.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = ManiskViolet, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = rule.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ManiskTextPrimary
                                )
                                Text(
                                    text = "Trigger: ${rule.triggerType}",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = ManiskTextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = rule.isEnabled,
                            onCheckedChange = { onToggleAutomation(rule) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ManiskCyan,
                                checkedTrackColor = ManiskCyan.copy(alpha = 0.3f),
                                uncheckedThumbColor = ManiskTextMuted,
                                uncheckedTrackColor = ManiskSurfaceBorder
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Actions: ${rule.actionsJson}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = ManiskTextMuted,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onTriggerAutomationMode(rule.mode) },
                            colors = ButtonDefaults.buttonColors(containerColor = ManiskCyan.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = ManiskCyan, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("RUN SCENE", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = ManiskCyan, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section: Devices Grid
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "IOT APPLIANCE MATRIX (${devices.size})",
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
                    if (isOn) ManiskCyan.copy(alpha = 0.4f) else ManiskSurfaceBorder
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
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isOn) ManiskCyan.copy(alpha = 0.2f) else ManiskSurfaceBorder),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isOn) ManiskCyan else ManiskTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = device.name,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = ManiskTextPrimary
                            )
                            Text(
                                text = "${device.room} • ${device.stateJson}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (isOn) ManiskEmerald else ManiskTextSecondary
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
                        modifier = Modifier.testTag("device_switch_${device.deviceId}")
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}
