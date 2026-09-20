package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TopOSBar
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.SecurityScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SmartHomeScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.screens.TerminalScreen
import com.example.ui.theme.ManiskCyan
import com.example.ui.theme.ManiskDarkBg
import com.example.ui.theme.ManiskSurface
import com.example.ui.theme.ManiskSurfaceBorder
import com.example.ui.theme.ManiskTextMuted
import com.example.ui.theme.ManiskTextPrimary
import com.example.ui.theme.ManiskTextSecondary
import com.example.ui.theme.ManiskTheme
import com.example.viewmodel.ManiskViewModel

enum class OSNavScreen(val title: String, val icon: ImageVector) {
    DASHBOARD("Overview", Icons.Default.Dashboard),
    TERMINAL("Terminal", Icons.Default.SmartToy),
    TASKS("Tasks", Icons.Default.TaskAlt),
    SMART_HOME("Devices", Icons.Default.Devices),
    SECURITY("Security", Icons.Default.Security),
    MEMORY("Memory", Icons.Default.Psychology),
    SETTINGS("Config", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    private val viewModel: ManiskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ManiskTheme {
                ManiskApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ManiskApp(viewModel: ManiskViewModel) {
    var currentScreen by remember { mutableStateOf(OSNavScreen.TERMINAL) }

    val autonomyLevel by viewModel.currentAutonomyLevel.collectAsState()
    val modelProvider by viewModel.currentModelProvider.collectAsState()
    val isEmergencyStopActive by viewModel.isEmergencyStopActive.collectAsState()

    val tasks by viewModel.allTasks.collectAsState()
    val events by viewModel.allEvents.collectAsState()
    val devices by viewModel.allDevices.collectAsState()
    val automations by viewModel.allAutomations.collectAsState()
    val permissions by viewModel.allPermissions.collectAsState()
    val auditLogs by viewModel.recentAuditLogs.collectAsState()
    val memories by viewModel.allMemories.collectAsState()
    val knowledgeDocs by viewModel.knowledgeDocs.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val messages by viewModel.messages.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val activeTelemetry by viewModel.activeTelemetry.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopOSBar(
                autonomyLevel = autonomyLevel,
                modelProvider = modelProvider,
                isEmergencyStopActive = isEmergencyStopActive,
                onEmergencyStopClicked = { viewModel.triggerEmergencyStop() },
                onResumeOperationsClicked = { viewModel.resumeNormalOperations() }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = ManiskSurface,
                modifier = Modifier.border(1.dp, ManiskSurfaceBorder)
            ) {
                OSNavScreen.values().forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF042F2E),
                            selectedTextColor = ManiskCyan,
                            indicatorColor = ManiskCyan,
                            unselectedIconColor = ManiskTextMuted,
                            unselectedTextColor = ManiskTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_${screen.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ManiskDarkBg)
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                OSNavScreen.DASHBOARD -> DashboardScreen(
                    tasks = tasks,
                    events = events,
                    devices = devices,
                    automations = automations,
                    recentLogs = auditLogs,
                    onToggleTask = { viewModel.toggleTaskCompletion(it) },
                    onToggleDevice = { viewModel.toggleSmartDevice(it) },
                    onNavigateToTerminal = { currentScreen = OSNavScreen.TERMINAL }
                )

                OSNavScreen.TERMINAL -> TerminalScreen(
                    messages = messages,
                    isProcessing = isProcessing,
                    activeTelemetry = activeTelemetry,
                    onSendMessage = { viewModel.processIntent(it) },
                    onAuthorizeStep = { step, session -> viewModel.authorizePendingStep(step, session) },
                    onDenyStep = { viewModel.denyPendingStep(it) }
                )

                OSNavScreen.TASKS -> TasksScreen(
                    tasks = tasks,
                    onToggleTask = { viewModel.toggleTaskCompletion(it) },
                    onAddTask = { title, priority, project -> viewModel.addTask(title, priority, project) }
                )

                OSNavScreen.SMART_HOME -> SmartHomeScreen(
                    devices = devices,
                    automations = automations,
                    onToggleDevice = { viewModel.toggleSmartDevice(it) },
                    onToggleAutomation = { viewModel.toggleAutomation(it) },
                    onTriggerAutomationMode = { mode ->
                        viewModel.processIntent("Activate $mode mode")
                    }
                )

                OSNavScreen.SECURITY -> SecurityScreen(
                    permissions = permissions,
                    auditLogs = auditLogs,
                    isEmergencyStopActive = isEmergencyStopActive,
                    onEmergencyStop = { viewModel.triggerEmergencyStop() },
                    onResumeNormal = { viewModel.resumeNormalOperations() },
                    onRevokeAll = { viewModel.revokeAllPermissions() },
                    onUpdatePermission = { key, granted, grantType ->
                        viewModel.updatePermission(key, granted, grantType)
                    }
                )

                OSNavScreen.MEMORY -> MemoryScreen(
                    memories = memories,
                    knowledgeDocs = knowledgeDocs,
                    onRemember = { content, cat -> viewModel.rememberFact(content, cat) },
                    onForget = { query -> viewModel.forgetMemory(query) }
                )

                OSNavScreen.SETTINGS -> SettingsScreen(
                    userProfile = userProfile,
                    currentProvider = modelProvider,
                    currentAutonomyLevel = autonomyLevel,
                    onSelectProvider = { viewModel.setModelProvider(it) },
                    onSelectAutonomyLevel = { viewModel.setAutonomyLevel(it) }
                )
            }
        }
    }
}
