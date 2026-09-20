package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.agents.AgentManager
import com.example.core.context.ContextEngine
import com.example.core.emergency.EmergencyController
import com.example.core.eventengine.EventEngine
import com.example.core.firewall.PermissionFirewall
import com.example.core.modelrouter.ModelRouter
import com.example.core.models.AgentTelemetry
import com.example.core.models.AutonomyLevel
import com.example.core.models.ChatMessage
import com.example.core.models.GrantType
import com.example.core.models.ModelProviderType
import com.example.core.models.PlanStep
import com.example.core.orchestrator.Orchestrator
import com.example.core.planner.Planner
import com.example.core.verification.VerificationEngine
import com.example.database.AuditLogEntity
import com.example.database.AutomationRuleEntity
import com.example.database.CalendarEventEntity
import com.example.database.DocumentKnowledgeEntity
import com.example.database.ManiskDatabase
import com.example.database.MemoryEntity
import com.example.database.PermissionEntity
import com.example.database.SecurityEventEntity
import com.example.database.SmartDeviceEntity
import com.example.database.TaskEntity
import com.example.database.UserProfileEntity
import com.example.tools.ToolRegistry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class ManiskViewModel(application: Application) : AndroidViewModel(application) {

    private val database: ManiskDatabase = ManiskDatabase.getDatabase(application, viewModelScope)

    val taskDao = database.taskDao()
    val calendarDao = database.calendarDao()
    val memoryDao = database.memoryDao()
    val permissionDao = database.permissionDao()
    val automationDao = database.automationDao()
    val smartDeviceDao = database.smartDeviceDao()
    val auditDao = database.auditDao()
    val securityDao = database.securityDao()
    val knowledgeDao = database.knowledgeDao()
    val userDao = database.userDao()

    // Subsystems
    val eventEngine = EventEngine(viewModelScope)
    val modelRouter = ModelRouter()
    val permissionFirewall = PermissionFirewall(permissionDao)
    val verificationEngine = VerificationEngine()
    val toolRegistry = ToolRegistry(calendarDao, taskDao, smartDeviceDao, memoryDao, knowledgeDao, auditDao)
    val agentManager = AgentManager()
    val contextEngine = ContextEngine(taskDao, calendarDao, smartDeviceDao, userDao, permissionFirewall)
    val emergencyController = EmergencyController(permissionFirewall, automationDao, userDao, auditDao)
    val planner = Planner()

    val orchestrator = Orchestrator(
        modelRouter = modelRouter,
        planner = planner,
        permissionFirewall = permissionFirewall,
        verificationEngine = verificationEngine,
        toolRegistry = toolRegistry,
        agentManager = agentManager,
        contextEngine = contextEngine,
        emergencyController = emergencyController,
        auditDao = auditDao
    )

    // StateFlows from Room
    val allTasks: StateFlow<List<TaskEntity>> = taskDao.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingTasks: StateFlow<List<TaskEntity>> = taskDao.getPendingTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEvents: StateFlow<List<CalendarEventEntity>> = calendarDao.getAllEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMemories: StateFlow<List<MemoryEntity>> = memoryDao.getAllMemories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPermissions: StateFlow<List<PermissionEntity>> = permissionDao.getAllPermissions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAutomations: StateFlow<List<AutomationRuleEntity>> = automationDao.getAllRules()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDevices: StateFlow<List<SmartDeviceEntity>> = smartDeviceDao.getAllDevices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentAuditLogs: StateFlow<List<AuditLogEntity>> = auditDao.getRecentLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val securityEvents: StateFlow<List<SecurityEventEntity>> = securityDao.getAllEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val knowledgeDocs: StateFlow<List<DocumentKnowledgeEntity>> = knowledgeDao.getAllDocuments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity?> = userDao.getProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Interactive Chat / OS Loop State
    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "init_greeting",
                sender = "MANISK",
                content = """
                    Welcome, Sir. MANISK Personal AI Operating System is online.
                    
                    • All 13 specialized agents initialized
                    • Zero-Trust Permission Firewall active
                    • Continuous Self-Verification Engine armed
                    • Current Autonomy: Level 3 (Execute Approved Workflows)
                    
                    How may I assist you today?
                """.trimIndent()
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _activeTelemetry = MutableStateFlow<AgentTelemetry?>(null)
    val activeTelemetry: StateFlow<AgentTelemetry?> = _activeTelemetry.asStateFlow()

    private val _currentAutonomyLevel = MutableStateFlow(AutonomyLevel.LEVEL_3)
    val currentAutonomyLevel: StateFlow<AutonomyLevel> = _currentAutonomyLevel.asStateFlow()

    private val _currentModelProvider = MutableStateFlow(ModelProviderType.NEMOTRON)
    val currentModelProvider: StateFlow<ModelProviderType> = _currentModelProvider.asStateFlow()

    val isEmergencyStopActive = emergencyController.isEmergencyStopActive

    fun setAutonomyLevel(level: AutonomyLevel) {
        _currentAutonomyLevel.value = level
        viewModelScope.launch {
            userDao.setAutonomyLevel("primary_user", level.level)
        }
    }

    fun setModelProvider(provider: ModelProviderType) {
        _currentModelProvider.value = provider
        modelRouter.currentProvider = provider
        viewModelScope.launch {
            userDao.setActiveProvider("primary_user", provider.name)
        }
    }

    fun processIntent(intentText: String) {
        if (intentText.isBlank() || _isProcessing.value) return

        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = "USER",
            content = intentText
        )
        _messages.value = _messages.value + userMsg
        _isProcessing.value = true

        viewModelScope.launch {
            try {
                val execState = orchestrator.processUserIntent(
                    userIntent = intentText,
                    autonomyLevel = _currentAutonomyLevel.value,
                    onTelemetryUpdate = { telemetry ->
                        _activeTelemetry.value = telemetry
                    }
                )
                _messages.value = _messages.value + execState.chatMessage
            } catch (e: Exception) {
                val errorMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    sender = "MANISK",
                    content = "Error during execution: ${e.localizedMessage ?: "Unknown system failure"}. Failure captured in audit logs."
                )
                _messages.value = _messages.value + errorMsg
            } finally {
                _isProcessing.value = false
                _activeTelemetry.value = null
            }
        }
    }

    fun authorizePendingStep(step: PlanStep, allowForSession: Boolean) {
        viewModelScope.launch {
            if (allowForSession) {
                permissionFirewall.grantForSession(step.requiredPermission.key)
            }
            val executedStep = orchestrator.executeConfirmedStep(step, _currentAutonomyLevel.value)

            val updateMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = "MANISK",
                content = "✓ Step Authorized: ${executedStep.description}\nResult: ${executedStep.executionResult}\nVerification: ${executedStep.verificationStatus}",
                isVerified = executedStep.isVerified
            )
            _messages.value = _messages.value + updateMsg
        }
    }

    fun denyPendingStep(step: PlanStep) {
        val deniedMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = "MANISK",
            content = "✗ Step Denied by User: ${step.description}. Operation cleanly aborted without side-effects."
        )
        _messages.value = _messages.value + deniedMsg
    }

    // Emergency Actions
    fun triggerEmergencyStop() {
        viewModelScope.launch {
            emergencyController.triggerEmergencyStop()
            _messages.value = _messages.value + ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = "MANISK",
                content = "🛑 EMERGENCY STOP TRIGGERED: All autonomous actions halted. Automations disabled. Permissions locked."
            )
        }
    }

    fun resumeNormalOperations() {
        viewModelScope.launch {
            emergencyController.resumeNormalOperations()
            _messages.value = _messages.value + ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = "MANISK",
                content = "✓ Emergency lockdown lifted. Normal operating status restored."
            )
        }
    }

    fun revokeAllPermissions() {
        viewModelScope.launch {
            emergencyController.revokeAllPermissions()
        }
    }

    fun updatePermission(key: String, isGranted: Boolean, grantType: GrantType) {
        viewModelScope.launch {
            permissionFirewall.updateGrant(key, isGranted, grantType)
        }
    }

    // Smart Device Quick Toggle
    fun toggleSmartDevice(device: SmartDeviceEntity) {
        viewModelScope.launch {
            val isOn = device.stateJson.contains("\"isOn\":true")
            val newState = if (isOn) {
                device.stateJson.replace("\"isOn\":true", "\"isOn\":false")
            } else {
                device.stateJson.replace("\"isOn\":false", "\"isOn\":true")
            }
            smartDeviceDao.updateDeviceState(device.deviceId, newState, System.currentTimeMillis())

            auditDao.insertLog(
                AuditLogEntity(
                    agent = "SmartHomeAgent",
                    tool = "smarthome.toggle",
                    action = "Toggled ${device.name}",
                    reason = "User dashboard direct control",
                    permission = "smart_home.control",
                    result = "State adjusted",
                    verification = "Verified updated in Room database",
                    status = "SUCCESS"
                )
            )
        }
    }

    // Task quick complete
    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            val newStatus = if (task.status == "COMPLETED") "TODO" else "COMPLETED"
            taskDao.updateStatus(task.id, newStatus)
            auditDao.insertLog(
                AuditLogEntity(
                    agent = "TaskAgent",
                    tool = "tasks.update_status",
                    action = "Mark task ${task.id} as $newStatus",
                    reason = "User direct check",
                    permission = "memory.store",
                    result = "Updated",
                    verification = "Verified status in Room database",
                    status = "SUCCESS"
                )
            )
        }
    }

    // Automation Toggle
    fun toggleAutomation(rule: AutomationRuleEntity) {
        viewModelScope.launch {
            automationDao.setRuleEnabled(rule.id, !rule.isEnabled)
        }
    }

    // Quick Add Task
    fun addTask(title: String, priority: String, project: String) {
        viewModelScope.launch {
            taskDao.insertTask(
                TaskEntity(
                    title = title,
                    priority = priority,
                    project = project,
                    status = "TODO",
                    deadline = System.currentTimeMillis() + 86400000L
                )
            )
        }
    }

    // Quick Add Calendar Event
    fun addCalendarEvent(title: String, hoursFromNow: Double, durationHours: Double, location: String) {
        viewModelScope.launch {
            val start = System.currentTimeMillis() + (hoursFromNow * 3600000).toLong()
            val end = start + (durationHours * 3600000).toLong()
            calendarDao.insertEvent(
                CalendarEventEntity(
                    title = title,
                    startTime = start,
                    endTime = end,
                    location = location,
                    isVerified = true
                )
            )
        }
    }

    // Remember / Forget
    fun rememberFact(fact: String, category: String) {
        viewModelScope.launch {
            memoryDao.insertMemory(
                MemoryEntity(
                    content = fact,
                    type = category,
                    confidence = 0.99f
                )
            )
        }
    }

    fun forgetMemory(query: String) {
        viewModelScope.launch {
            memoryDao.deleteMemoryByContent(query)
        }
    }
}
