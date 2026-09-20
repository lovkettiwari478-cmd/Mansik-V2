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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import com.example.core.models.AutonomyLevel
import com.example.core.models.ModelProviderType
import com.example.database.UserProfileEntity
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
fun SettingsScreen(
    userProfile: UserProfileEntity?,
    currentProvider: ModelProviderType,
    currentAutonomyLevel: AutonomyLevel,
    onSelectProvider: (ModelProviderType) -> Unit,
    onSelectAutonomyLevel: (AutonomyLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    val providers = listOf(
        ModelProviderType.NEMOTRON to "NVIDIA Nemotron (Primary OS Reasoning Engine)",
        ModelProviderType.GEMINI to "Google Gemini 2.0 Flash (Multimodal & Fast)",
        ModelProviderType.OPENAI to "OpenAI GPT-4o (General High-Yield Tasks)",
        ModelProviderType.ANTHROPIC to "Anthropic Claude 3.5 Sonnet (Nuanced Analysis)",
        ModelProviderType.DEEPSEEK to "DeepSeek Reasoner (Complex Planning)"
    )

    val autonomyLevels = listOf(
        AutonomyLevel.LEVEL_0 to "Suggestion Only: Never executes tools autonomously",
        AutonomyLevel.LEVEL_1 to "Plan Only: Proposes plans; requires user to trigger every step",
        AutonomyLevel.LEVEL_2 to "Ask Before Action: Asks confirmation before each tool",
        AutonomyLevel.LEVEL_3 to "Execute Approved Workflows: Runs low/medium tools, confirms high/critical",
        AutonomyLevel.LEVEL_4 to "High Autonomy: Full execution within strict permission boundaries"
    )

    val specializedAgents = listOf(
        "PlannerAgent" to "Goal decomposition & milestone validation",
        "MemoryAgent" to "Episodic facts & retention policy",
        "ResearchAgent" to "Multi-source factual retrieval",
        "CalendarAgent" to "Schedule orchestration & conflict check",
        "TaskAgent" to "Priority queue & deadline tracking",
        "CommunicationAgent" to "Drafting & guarded outbox",
        "SmartHomeAgent" to "Appliance control & safety bounds",
        "ComputerAgent" to "Sandboxed execution & file inspection",
        "KnowledgeAgent" to "Document chunking & RAG indexing",
        "CreativeAgent" to "Visual prompting & narrative synthesis",
        "SecurityAgent" to "Zero-trust audit & escalation blocker",
        "MonitoringAgent" to "Telemetry tracking & heartbeat",
        "AutomationAgent" to "Contextual scenes & state coordination"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ManiskDarkBg)
            .padding(16.dp)
    ) {
        // User Identity Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ManiskSurface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ManiskSurfaceBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ManiskCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = ManiskCyan, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = userProfile?.name ?: "Anita",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ManiskTextPrimary
                        )
                        Text(
                            text = "${userProfile?.email ?: "anita94067@gmail.com"} • Primary Tenant",
                            fontSize = 12.sp,
                            color = ManiskTextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Section: Model Router Provider Selection
        item {
            Text(
                text = "MODEL ROUTER SELECTION",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ManiskTextMuted,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(providers) { (provider, desc) ->
            val isSelected = currentProvider == provider
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSelectProvider(provider) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) ManiskSurfaceElevated else ManiskSurface
                ),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(
                    0.5.dp,
                    if (isSelected) ManiskCyan else ManiskSurfaceBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectProvider(provider) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = ManiskCyan,
                            unselectedColor = ManiskTextMuted
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = provider.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (isSelected) ManiskCyan else ManiskTextPrimary
                        )
                        Text(
                            text = desc,
                            fontSize = 11.sp,
                            color = ManiskTextSecondary
                        )
                    }
                }
            }
        }

        // Section: Autonomy Level Matrix
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "AUTONOMY LEVEL SPECIFICATION",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ManiskTextMuted,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(autonomyLevels) { (lvl, desc) ->
            val isSelected = currentAutonomyLevel == lvl
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSelectAutonomyLevel(lvl) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) ManiskSurfaceElevated else ManiskSurface
                ),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(
                    0.5.dp,
                    if (isSelected) ManiskCyan else ManiskSurfaceBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectAutonomyLevel(lvl) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = ManiskCyan,
                            unselectedColor = ManiskTextMuted
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Level ${lvl.level}: ${lvl.title}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (isSelected) ManiskCyan else ManiskTextPrimary
                        )
                        Text(
                            text = desc,
                            fontSize = 11.sp,
                            color = ManiskTextSecondary
                        )
                    }
                }
            }
        }

        // Section: Registered Specialized Agents Telemetry
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "REGISTERED SPECIALIZED AGENTS (13)",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ManiskTextMuted,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(specializedAgents) { (name, role) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                colors = CardDefaults.cardColors(containerColor = ManiskSurfaceElevated),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = ManiskCyan
                        )
                        Text(
                            text = role,
                            fontSize = 10.sp,
                            color = ManiskTextSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(ManiskEmerald.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "BOUNDED",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = ManiskEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}
