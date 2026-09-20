package com.example.core.models

/**
 * MANISK — Personal AI Operating System
 * Core Domain Models & Enumerations
 */

enum class RiskLevel(val label: String, val badgeColor: Long) {
    LOW("Low Risk", 0xFF10B981),        // Emerald
    MEDIUM("Medium Risk", 0xFFF59E0B),  // Amber
    HIGH("High Risk", 0xFFF97316),      // Orange
    CRITICAL("Critical Risk", 0xFFEF4444) // Red
}

enum class AutonomyLevel(val level: Int, val title: String, val description: String) {
    LEVEL_0(0, "Level 0: Answer Only", "Only provides text answers. Never invokes tools."),
    LEVEL_1(1, "Level 1: Suggest", "Suggests actions and plans but does not execute."),
    LEVEL_2(2, "Level 2: Ask Before Action", "Drafts plans and asks for confirmation before any tool run."),
    LEVEL_3(3, "Level 3: Execute Approved", "Executes low/medium approved tools automatically; asks for high/critical."),
    LEVEL_4(4, "Level 4: Autonomous", "Executes within strict user-defined automation boundaries.")
}

enum class PermissionType(val key: String, val displayName: String, val risk: RiskLevel, val description: String) {
    CALENDAR_READ("calendar.read", "Read Calendar", RiskLevel.LOW, "Access agenda and scheduled events"),
    CALENDAR_WRITE("calendar.write", "Modify Calendar", RiskLevel.MEDIUM, "Create or reschedule calendar events"),
    EMAIL_READ("email.read", "Read Email", RiskLevel.LOW, "Access inbox and read authorized messages"),
    EMAIL_DRAFT("email.draft", "Draft Email", RiskLevel.MEDIUM, "Compose email drafts for user review"),
    EMAIL_SEND("email.send", "Send Email", RiskLevel.HIGH, "Dispatch emails directly to external contacts"),
    MESSAGES_READ("messages.read", "Read Messages", RiskLevel.LOW, "Inspect recent communication logs"),
    MESSAGES_SEND("messages.send", "Send Messages", RiskLevel.HIGH, "Send outgoing direct messages"),
    CAMERA_USE("camera.use", "Camera Access", RiskLevel.HIGH, "Capture visual input for identity or vision"),
    MICROPHONE_USE("microphone.use", "Microphone Access", RiskLevel.HIGH, "Capture real-time voice speech"),
    SMART_HOME_READ("smart_home.read", "Read Smart Home", RiskLevel.LOW, "Query telemetry from connected IoT devices"),
    SMART_HOME_CONTROL("smart_home.control", "Control Smart Home", RiskLevel.MEDIUM, "Toggle and adjust smart appliances"),
    COMPUTER_BROWSER("computer.browser", "Computer Browser", RiskLevel.HIGH, "Automate browser interactions"),
    COMPUTER_FILES("computer.files", "Computer File System", RiskLevel.CRITICAL, "Read, write or delete local files"),
    LOCATION_USE("location.use", "Location Telemetry", RiskLevel.LOW, "Access geofence and presence information"),
    MEMORY_STORE("memory.store", "Store Memory", RiskLevel.LOW, "Persist learned user facts and preferences"),
    MEMORY_DELETE("memory.delete", "Purge Memory", RiskLevel.MEDIUM, "Permanently delete stored memories"),
    IMAGE_GENERATE("image.generate", "Image Generation", RiskLevel.MEDIUM, "Invoke generative visual synthesis")
}

enum class GrantType {
    ALLOW,
    DENY,
    ASK_EVERY_TIME,
    SESSION,
    UNTIL_TIMESTAMP,
    REVOKED
}

enum class MemoryCategory(val displayName: String) {
    SHORT_TERM("Short Term"),
    LONG_TERM("Long Term"),
    PREFERENCES("Preferences"),
    FACTS("Facts"),
    PROJECTS("Projects"),
    TASKS("Tasks"),
    EVENTS("Events"),
    CONVERSATION_CONTEXT("Conversation Context")
}

enum class TaskStatus(val displayName: String) {
    TODO("To Do"),
    IN_PROGRESS("In Progress"),
    BLOCKED("Blocked"),
    COMPLETED("Completed"),
    OVERDUE("Overdue"),
    CANCELLED("Cancelled")
}

enum class TaskPriority(val displayName: String, val level: Int) {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3),
    URGENT("Urgent", 4)
}

enum class AutomationMode(val displayName: String, val description: String) {
    WELCOME_HOME("Welcome Home", "Adjusts lights, AC, and announces greeting upon arrival"),
    LEAVING_HOME("Leaving Home", "Secures doors, turns off appliances, arms security"),
    MEETING("Meeting Mode", "Silences notifications, sets optimal lighting, readies briefing"),
    STUDY("Study / Focus", "Blocks distractions, sets white noise, prioritizes study tasks"),
    SLEEP("Sleep Sanctuary", "Dims lighting, locks thermostats to night curve, silences notifications"),
    MOVIE("Cinema Mode", "Dims living room lights, turns on home theater"),
    FOCUS("Deep Focus", "Strict isolation mode with high priority filters"),
    ENERGY_SAVER("Eco Saver", "Minimizes standby power and manages HVAC efficiency"),
    CUSTOM("Custom Mode", "User-defined environmental and task automation")
}

enum class ModelProviderType(val displayName: String, val defaultModel: String) {
    NEMOTRON("NVIDIA Nemotron", "nvidia/nemotron-4-340b-instruct"),
    GEMINI("Google Gemini", "gemini-2.0-flash"),
    OPENAI("OpenAI", "gpt-4o"),
    ANTHROPIC("Anthropic", "claude-3-5-sonnet"),
    DEEPSEEK("DeepSeek", "deepseek-reasoner")
}

enum class TaskRoutingCategory(val displayName: String) {
    FAST("Fast / Latency-Critical"),
    REASONING("Deep Reasoning & Planning"),
    CODING("Code Synthesis & Tool Integration"),
    VISION("Multimodal & Visual Analysis"),
    CREATIVE("Creative Writing & Synthesis")
}

data class PlanStep(
    val id: String,
    val stepIndex: Int,
    val agentName: String,
    val toolName: String,
    val description: String,
    val requiredPermission: PermissionType,
    val arguments: Map<String, Any> = emptyMap(),
    val dependencies: List<String> = emptyList(),
    val expectedOutcome: String,
    var isApproved: Boolean = false,
    var isExecuted: Boolean = false,
    var executionResult: String? = null,
    var verificationStatus: String? = null,
    var isVerified: Boolean = false
)

data class ExecutionPlan(
    val goal: String,
    val constraints: List<String>,
    val steps: List<PlanStep>,
    val riskSummary: RiskLevel,
    val requiresExplicitApproval: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class VerificationResult(
    val success: Boolean,
    val message: String,
    val expectedState: String,
    val actualState: String,
    val evidence: String,
    val retryCount: Int = 0
)

data class OSContext(
    val currentTimeString: String,
    val userProfileName: String,
    val activeTasksCount: Int,
    val upcomingEventsCount: Int,
    val activeAutomationMode: AutomationMode?,
    val connectedDevicesCount: Int,
    val securityStatus: String,
    val autonomyLevel: AutonomyLevel,
    val activeModelProvider: ModelProviderType
)

data class AgentTelemetry(
    val stepName: String,
    val detail: String,
    val status: String, // "ANALYZING", "PLANNING", "AWAITING_PERM", "EXECUTING", "VERIFYING", "COMPLETED", "FAILED"
    val timestamp: Long = System.currentTimeMillis()
)

data class ChatMessage(
    val id: String,
    val sender: String, // "USER" or "MANISK"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val telemetry: List<AgentTelemetry> = emptyList(),
    val plan: ExecutionPlan? = null,
    val pendingPermissionStep: PlanStep? = null,
    val isVerified: Boolean = false,
    val verificationDetail: String? = null
)
