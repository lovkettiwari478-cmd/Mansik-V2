package com.example.agents

import com.example.core.models.PermissionType
import com.example.core.models.RiskLevel

interface ManiskAgent {
    val name: String
    val description: String
    val capabilities: List<String>
    val requiredPermissions: List<PermissionType>
    val availableTools: List<String>
    val riskLevel: RiskLevel
    val inputSchema: String
    val outputSchema: String
}

class PlannerAgent : ManiskAgent {
    override val name = "PlannerAgent"
    override val description = "Converts high-level goals into executable, dependency-checked task graphs."
    override val capabilities = listOf("Goal Decomposition", "Constraint Verification", "Dependency Mapping", "Milestone Generation")
    override val requiredPermissions = listOf(PermissionType.CALENDAR_READ, PermissionType.MEMORY_STORE)
    override val availableTools = listOf("tasks.create_task", "calendar.query")
    override val riskLevel = RiskLevel.LOW
    override val inputSchema = "{ goal: String, constraints: List<String> }"
    override val outputSchema = "{ plan: ExecutionPlan, milestones: List<Task> }"
}

class MemoryAgent : ManiskAgent {
    override val name = "MemoryAgent"
    override val description = "Manages episodic memory, facts, user preferences, and sensitive data retention."
    override val capabilities = listOf("Episodic Memory", "Preference Recall", "Confidence Scoring", "GDPR-compliant Purge")
    override val requiredPermissions = listOf(PermissionType.MEMORY_STORE, PermissionType.MEMORY_DELETE)
    override val availableTools = listOf("memory.remember", "memory.forget")
    override val riskLevel = RiskLevel.MEDIUM
    override val inputSchema = "{ content: String, category: String, sensitivity: String }"
    override val outputSchema = "{ memoryId: Long, verified: Boolean }"
}

class ResearchAgent : ManiskAgent {
    override val name = "ResearchAgent"
    override val description = "Conducts multi-source information retrieval, source comparison, and non-hallucinatory synthesis."
    override val capabilities = listOf("Web Research", "Source Comparison", "Contradiction Detection", "Citation Extraction")
    override val requiredPermissions = listOf(PermissionType.LOCATION_USE)
    override val availableTools = listOf("research.search")
    override val riskLevel = RiskLevel.LOW
    override val inputSchema = "{ query: String, depth: String }"
    override val outputSchema = "{ sources: List<Source>, summary: String }"
}

class CalendarAgent : ManiskAgent {
    override val name = "CalendarAgent"
    override val description = "Handles schedule orchestration, conflict detection, travel buffer, and verified event booking."
    override val capabilities = listOf("Schedule Management", "Conflict Detection", "Preparation Buffers", "Event Verification")
    override val requiredPermissions = listOf(PermissionType.CALENDAR_READ, PermissionType.CALENDAR_WRITE)
    override val availableTools = listOf("calendar.create_event", "calendar.query")
    override val riskLevel = RiskLevel.MEDIUM
    override val inputSchema = "{ title: String, startOffsetHours: Double, durationHours: Double }"
    override val outputSchema = "{ eventId: Long, status: String }"
}

class TaskAgent : ManiskAgent {
    override val name = "TaskAgent"
    override val description = "Tracks priority queues, deadline monitoring, task status transitions, and backlog management."
    override val capabilities = listOf("Task Priority Scheduling", "Overdue Detection", "Status Transitions", "Dependency Verification")
    override val requiredPermissions = listOf(PermissionType.MEMORY_STORE)
    override val availableTools = listOf("tasks.create_task")
    override val riskLevel = RiskLevel.LOW
    override val inputSchema = "{ title: String, priority: String, estimatedDuration: String }"
    override val outputSchema = "{ taskId: Long, status: String }"
}

class CommunicationAgent : ManiskAgent {
    override val name = "CommunicationAgent"
    override val description = "Composes message drafts, manages external communication, and guarantees Draft != Send."
    override val capabilities = listOf("Email Drafting", "Message Summarization", "Outbox Dispatch (Guarded)", "Contact Mapping")
    override val requiredPermissions = listOf(PermissionType.EMAIL_DRAFT, PermissionType.EMAIL_SEND)
    override val availableTools = listOf("communication.draft", "communication.send")
    override val riskLevel = RiskLevel.HIGH
    override val inputSchema = "{ recipient: String, subject: String, body: String, sendImmediately: Boolean }"
    override val outputSchema = "{ draftId: String, dispatched: Boolean }"
}

class SmartHomeAgent : ManiskAgent {
    override val name = "SmartHomeAgent"
    override val description = "Controls authorized IoT appliances, checks safety ranges, and handles ambient environmental modes."
    override val capabilities = listOf("Appliance Control", "Safety Bounds Check", "Scene Execution", "Sensor Reading")
    override val requiredPermissions = listOf(PermissionType.SMART_HOME_READ, PermissionType.SMART_HOME_CONTROL)
    override val availableTools = listOf("smarthome.set_state")
    override val riskLevel = RiskLevel.MEDIUM
    override val inputSchema = "{ deviceId: String, stateJson: String }"
    override val outputSchema = "{ confirmedState: String, verified: Boolean }"
}

class ComputerAgent : ManiskAgent {
    override val name = "ComputerAgent"
    override val description = "Sandboxed computer tool operations, file inspections, and workflow automation."
    override val capabilities = listOf("File Inspection", "Sandboxed Execution", "Workflow Scripts", "Integrity Audits")
    override val requiredPermissions = listOf(PermissionType.COMPUTER_FILES, PermissionType.COMPUTER_BROWSER)
    override val availableTools = listOf("computer.inspect_state")
    override val riskLevel = RiskLevel.CRITICAL
    override val inputSchema = "{ operation: String, path: String }"
    override val outputSchema = "{ result: String, status: String }"
}

class KnowledgeAgent : ManiskAgent {
    override val name = "KnowledgeAgent"
    override val description = "Indexes local documents, extracts chunks, tracks source references, and powers document RAG."
    override val capabilities = listOf("Document Indexing", "Semantic Chunking", "Citation Tracking", "Keyword Retrieval")
    override val requiredPermissions = listOf(PermissionType.MEMORY_STORE)
    override val availableTools = listOf("knowledge.index")
    override val riskLevel = RiskLevel.LOW
    override val inputSchema = "{ title: String, content: String, category: String }"
    override val outputSchema = "{ docId: Long, chunksIndexed: Int }"
}

class CreativeAgent : ManiskAgent {
    override val name = "CreativeAgent"
    override val description = "Synthesizes creative proposals, visual asset prompts, and narrative composition."
    override val capabilities = listOf("Creative Ideation", "Prompt Engineering", "Narrative Drafting", "Visual Conceptualization")
    override val requiredPermissions = listOf(PermissionType.IMAGE_GENERATE)
    override val availableTools: List<String> = emptyList()
    override val riskLevel = RiskLevel.MEDIUM
    override val inputSchema = "{ concept: String, style: String }"
    override val outputSchema = "{ creativeOutput: String }"
}

class SecurityAgent : ManiskAgent {
    override val name = "SecurityAgent"
    override val description = "Continuously audits permission matrices, detects privilege escalation, and monitors anomalies."
    override val capabilities = listOf("Privilege Escalation Detection", "Zero-Trust Enforcement", "Emergency Lockdown", "Audit Verification")
    override val requiredPermissions = listOf(PermissionType.MEMORY_STORE)
    override val availableTools: List<String> = emptyList()
    override val riskLevel = RiskLevel.CRITICAL
    override val inputSchema = "{ targetAgent: String, requestedPermission: String }"
    override val outputSchema = "{ authorized: Boolean, threatLevel: String }"
}

class MonitoringAgent : ManiskAgent {
    override val name = "MonitoringAgent"
    override val description = "Tracks telemetry from agents, tools, device sensors, and system health status."
    override val capabilities = listOf("System Telemetry", "Latency Tracking", "Failure Detection", "Health Heartbeats")
    override val requiredPermissions: List<PermissionType> = emptyList()
    override val availableTools: List<String> = emptyList()
    override val riskLevel = RiskLevel.LOW
    override val inputSchema = "{ metric: String }"
    override val outputSchema = "{ health: String, uptime: Long }"
}

class AutomationAgent : ManiskAgent {
    override val name = "AutomationAgent"
    override val description = "Executes contextual automation triggers (Welcome Home, Meeting Mode, Sleep Sanctuary) with post-verification."
    override val capabilities = listOf("Trigger Evaluation", "State Machine Coordination", "Multi-Device Scenes", "Outcome Verification")
    override val requiredPermissions = listOf(PermissionType.SMART_HOME_CONTROL, PermissionType.CALENDAR_READ)
    override val availableTools = listOf("smarthome.set_state")
    override val riskLevel = RiskLevel.MEDIUM
    override val inputSchema = "{ mode: String, triggerContext: Map<String, Any> }"
    override val outputSchema = "{ modeActivated: Boolean, verifiedDevices: List<String> }"
}

class AgentManager {
    val allAgents: List<ManiskAgent> = listOf(
        PlannerAgent(),
        MemoryAgent(),
        ResearchAgent(),
        CalendarAgent(),
        TaskAgent(),
        CommunicationAgent(),
        SmartHomeAgent(),
        ComputerAgent(),
        KnowledgeAgent(),
        CreativeAgent(),
        SecurityAgent(),
        MonitoringAgent(),
        AutomationAgent()
    )

    private val agentMap = allAgents.associateBy { it.name }

    fun getAgent(name: String): ManiskAgent? = agentMap[name]
}
