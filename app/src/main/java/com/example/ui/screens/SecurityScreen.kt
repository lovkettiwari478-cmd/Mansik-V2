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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import com.example.core.models.GrantType
import com.example.core.models.RiskLevel
import com.example.database.AuditLogEntity
import com.example.database.PermissionEntity
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SecurityScreen(
    permissions: List<PermissionEntity>,
    auditLogs: List<AuditLogEntity>,
    isEmergencyStopActive: Boolean,
    onEmergencyStop: () -> Unit,
    onResumeNormal: () -> Unit,
    onRevokeAll: () -> Unit,
    onUpdatePermission: (String, Boolean, GrantType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ManiskDarkBg)
            .padding(16.dp)
    ) {
        // Top Security Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isEmergencyStopActive) ManiskCrimson else ManiskEmerald, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = ManiskSurface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (isEmergencyStopActive) ManiskCrimson else ManiskEmerald,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isEmergencyStopActive) "EMERGENCY LOCKDOWN ACTIVE" else "ZERO-TRUST PERMISSION FIREWALL",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isEmergencyStopActive) ManiskCrimson else ManiskEmerald
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "MANISK enforces strict granular permissions. No agent can escalate privileges silently, access unauthorized sensors, or bypass user confirmation.",
                        fontSize = 12.sp,
                        color = ManiskTextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Emergency Action Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!isEmergencyStopActive) {
                            Button(
                                onClick = onEmergencyStop,
                                colors = ButtonDefaults.buttonColors(containerColor = ManiskCrimson),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("security_panic_stop")
                            ) {
                                Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("STOP AGENTS", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = onResumeNormal,
                                colors = ButtonDefaults.buttonColors(containerColor = ManiskEmerald),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("security_resume")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("RESUME SYSTEM", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedButton(
                            onClick = onRevokeAll,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ManiskAmber),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("security_revoke_all")
                        ) {
                            Icon(Icons.Default.LockReset, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("REVOKE ALL", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: Granular Permissions
        item {
            Text(
                text = "GRANULAR PERMISSION MATRIX (${permissions.size})",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ManiskTextMuted,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(permissions) { perm ->
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
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = perm.displayName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ManiskTextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        when (perm.riskLevel) {
                                            "CRITICAL" -> ManiskCrimson.copy(alpha = 0.2f)
                                            "HIGH" -> ManiskAmber.copy(alpha = 0.2f)
                                            else -> ManiskEmerald.copy(alpha = 0.2f)
                                        }
                                    )
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = perm.riskLevel,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = when (perm.riskLevel) {
                                        "CRITICAL" -> ManiskCrimson
                                        "HIGH" -> ManiskAmber
                                        else -> ManiskEmerald
                                    }
                                )
                            }
                        }
                        Text(
                            text = "Key: ${perm.permissionKey} • Mode: ${perm.grantType}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = ManiskTextSecondary
                        )
                    }

                    Switch(
                        checked = perm.isGranted,
                        onCheckedChange = { isChecked ->
                            val newGrant = if (isChecked) GrantType.ALLOW else GrantType.DENY
                            onUpdatePermission(perm.permissionKey, isChecked, newGrant)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ManiskCyan,
                            checkedTrackColor = ManiskCyan.copy(alpha = 0.3f),
                            uncheckedThumbColor = ManiskTextMuted,
                            uncheckedTrackColor = ManiskSurfaceBorder
                        ),
                        modifier = Modifier.testTag("perm_toggle_${perm.permissionKey}")
                    )
                }
            }
        }

        // Section: Immutable Audit Logs
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "IMMUTABLE AUDIT LOG",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ManiskTextMuted,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${auditLogs.size} Records",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = ManiskCyan
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(auditLogs) { log ->
            val timeFmt = SimpleDateFormat("h:mm:ss a", Locale.getDefault())
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = ManiskSurfaceElevated),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, ManiskSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${timeFmt.format(Date(log.timestamp))} • [${log.agent}]",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ManiskCyan
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
                        text = log.action,
                        fontSize = 12.sp,
                        color = ManiskTextPrimary
                    )
                    Text(
                        text = "Tool: ${log.tool} | Perm: ${log.permission}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = ManiskTextSecondary
                    )
                    Text(
                        text = "Evidence: ${log.verification}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = ManiskEmerald
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}
