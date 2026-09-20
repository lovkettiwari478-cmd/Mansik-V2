package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.models.AgentTelemetry
import com.example.core.models.ChatMessage
import com.example.core.models.PlanStep
import com.example.core.models.RiskLevel
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

@Composable
fun TerminalScreen(
    messages: List<ChatMessage>,
    isProcessing: Boolean,
    activeTelemetry: AgentTelemetry?,
    onSendMessage: (String) -> Unit,
    onAuthorizeStep: (PlanStep, Boolean) -> Unit,
    onDenyStep: (PlanStep) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val presetChips = listOf(
        "Prepare everything for my meeting tomorrow.",
        "I have an exam Friday. Help me prepare.",
        "Turn on AC and activate Welcome Home.",
        "Remember my project deadline is Oct 15.",
        "Show security audit trail."
    )

    LaunchedEffect(messages.size, isProcessing) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ManiskDarkBg)
    ) {
        // Active Telemetry Monitor Bar (if processing)
        AnimatedVisibility(visible = isProcessing) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ManiskSurfaceElevated)
                    .border(0.5.dp, ManiskCyan.copy(alpha = 0.5f))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    strokeWidth = 2.dp,
                    color = ManiskCyan
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "MANISK COGNITIVE LOOP: ${activeTelemetry?.stepName ?: "PROCESSING"}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ManiskCyan
                    )
                    Text(
                        text = activeTelemetry?.detail ?: "Evaluating intent, context, and permissions...",
                        fontSize = 10.sp,
                        color = ManiskTextSecondary,
                        maxLines = 1
                    )
                }
            }
        }

        // Messages Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(10.dp)) }

            items(messages) { msg ->
                if (msg.sender == "USER") {
                    UserMessageBubble(msg)
                } else {
                    ManiskMessageCard(
                        msg = msg,
                        onAuthorize = onAuthorizeStep,
                        onDeny = onDenyStep
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            item { Spacer(modifier = Modifier.height(10.dp)) }
        }

        // Preset Prompt Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            items(presetChips) { prompt ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(ManiskSurface)
                        .border(1.dp, ManiskSurfaceBorder, RoundedCornerShape(16.dp))
                        .clickable {
                            inputText = prompt
                            onSendMessage(prompt)
                            inputText = ""
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = prompt,
                        fontSize = 11.sp,
                        color = ManiskTextSecondary
                    )
                }
            }
        }

        // Input Field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ManiskSurface)
                .border(1.dp, ManiskSurfaceBorder)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        text = "Command MANISK (e.g. 'Prepare my meeting', 'Remember...')",
                        fontSize = 12.sp,
                        color = ManiskTextMuted
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("terminal_input_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ManiskCyan,
                    unfocusedBorderColor = ManiskSurfaceBorder,
                    focusedTextColor = ManiskTextPrimary,
                    unfocusedTextColor = ManiskTextPrimary,
                    cursorColor = ManiskCyan
                ),
                maxLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (inputText.isNotBlank() && !isProcessing) {
                        onSendMessage(inputText)
                        inputText = ""
                    }
                })
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputText.isNotBlank() && !isProcessing) {
                        onSendMessage(inputText)
                        inputText = ""
                    }
                },
                enabled = inputText.isNotBlank() && !isProcessing,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (inputText.isNotBlank() && !isProcessing) ManiskCyan else ManiskSurfaceElevated)
                    .testTag("terminal_send_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Intent",
                    tint = if (inputText.isNotBlank() && !isProcessing) Color(0xFF042F2E) else ManiskTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun UserMessageBubble(msg: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(14.dp, 14.dp, 2.dp, 14.dp))
                .background(ManiskIndigo.copy(alpha = 0.3f))
                .border(1.dp, ManiskIndigo.copy(alpha = 0.5f), RoundedCornerShape(14.dp, 14.dp, 2.dp, 14.dp))
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = "USER INTENT",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = ManiskViolet,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = msg.content,
                    fontSize = 13.sp,
                    color = ManiskTextPrimary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun ManiskMessageCard(
    msg: ChatMessage,
    onAuthorize: (PlanStep, Boolean) -> Unit,
    onDeny: (PlanStep) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ManiskSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ManiskSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(ManiskCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = ManiskCyan,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MANISK OPERATING SYSTEM",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = ManiskCyan
                    )
                }

                if (msg.isVerified) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = ManiskEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "VERIFIED",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ManiskEmerald
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body text
            Text(
                text = msg.content,
                fontSize = 13.sp,
                color = ManiskTextPrimary,
                lineHeight = 19.sp
            )

            // Execution Plan Breakdown if attached
            if (msg.plan != null && msg.plan.steps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                PlanCardView(msg.plan)
            }

            // Interactive Permission Confirmation Request Card
            if (msg.pendingPermissionStep != null) {
                Spacer(modifier = Modifier.height(14.dp))
                InteractivePermissionCard(
                    step = msg.pendingPermissionStep,
                    onAuthorize = onAuthorize,
                    onDeny = onDeny
                )
            }

            // Verification Details / Evidence
            if (msg.verificationDetail != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(ManiskSurfaceElevated)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Evidence: ${msg.verificationDetail}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = ManiskTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun PlanCardView(plan: com.example.core.models.ExecutionPlan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ManiskSurfaceElevated),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, ManiskCyan.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STRUCTURED EXECUTION PLAN",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ManiskCyan
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when (plan.riskSummary) {
                                RiskLevel.CRITICAL -> ManiskCrimson.copy(alpha = 0.2f)
                                RiskLevel.HIGH -> ManiskAmber.copy(alpha = 0.2f)
                                else -> ManiskEmerald.copy(alpha = 0.2f)
                            }
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = plan.riskSummary.label,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = when (plan.riskSummary) {
                            RiskLevel.CRITICAL -> ManiskCrimson
                            RiskLevel.HIGH -> ManiskAmber
                            else -> ManiskEmerald
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            plan.steps.forEach { step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${step.stepIndex}.",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = ManiskTextMuted,
                        modifier = Modifier.width(20.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = step.description,
                            fontSize = 12.sp,
                            color = ManiskTextPrimary
                        )
                        Text(
                            text = "Agent: ${step.agentName} • Req: ${step.requiredPermission.key}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = ManiskTextSecondary
                        )
                    }
                    if (step.isVerified) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Verified",
                            tint = ManiskEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InteractivePermissionCard(
    step: PlanStep,
    onAuthorize: (PlanStep, Boolean) -> Unit,
    onDeny: (PlanStep) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1917)),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ManiskAmber)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = ManiskAmber,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "AUTHORIZATION REQUIRED",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ManiskAmber
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Action: ${step.description}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = ManiskTextPrimary
            )
            Text(
                text = "Permission: ${step.requiredPermission.displayName} (${step.requiredPermission.key})",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = ManiskTextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = { onDeny(step) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ManiskCrimson),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("DENY", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { onAuthorize(step, true) },
                    colors = ButtonDefaults.buttonColors(containerColor = ManiskCyan),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        "ALLOW SESSION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF042F2E)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { onAuthorize(step, false) },
                    colors = ButtonDefaults.buttonColors(containerColor = ManiskEmerald),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        "ALLOW ONCE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White
                    )
                }
            }
        }
    }
}
