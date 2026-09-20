package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.core.models.GrantType
import com.example.core.models.MemoryCategory
import com.example.core.models.RiskLevel
import com.example.core.models.TaskPriority
import com.example.core.models.TaskStatus

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val priority: String = TaskPriority.MEDIUM.name,
    val deadline: Long = 0L,
    val status: String = TaskStatus.TODO.name,
    val dependencies: String = "", // Comma-separated IDs
    val estimatedDuration: String = "30m",
    val project: String = "General",
    val reminders: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "calendar_events")
data class CalendarEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val startTime: Long,
    val endTime: Long,
    val location: String = "",
    val isVerified: Boolean = true,
    val reminderMinutes: Int = 15,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "primary_user",
    val type: String = MemoryCategory.FACTS.name,
    val content: String,
    val source: String = "User Conversation",
    val confidence: Float = 0.95f,
    val sensitivity: String = "NORMAL", // "PUBLIC", "NORMAL", "CONFIDENTIAL", "HIGH_SECURITY"
    val retentionPolicy: String = "INDEFINITE",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "permissions")
data class PermissionEntity(
    @PrimaryKey val permissionKey: String,
    val displayName: String,
    val riskLevel: String = RiskLevel.LOW.name,
    val isGranted: Boolean = false,
    val grantType: String = GrantType.ASK_EVERY_TIME.name,
    val grantedUntil: Long = 0L
)

@Entity(tableName = "automations")
data class AutomationRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val mode: String, // e.g., WELCOME_HOME, MEETING, etc.
    val triggerType: String, // e.g., "TIME", "LOCATION_ARRIVAL", "CALENDAR_APPROACHING"
    val conditionsJson: String,
    val actionsJson: String,
    val isEnabled: Boolean = true,
    val lastTriggered: Long = 0L
)

@Entity(tableName = "smart_devices")
data class SmartDeviceEntity(
    @PrimaryKey val deviceId: String,
    val name: String,
    val type: String, // "AC", "LIGHT", "FAN", "TV", "THERMOSTAT", "CAMERA", "SENSOR"
    val room: String,
    val stateJson: String, // e.g. {"isOn": true, "temp": 22, "brightness": 80}
    val isOnline: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val agent: String,
    val tool: String,
    val action: String,
    val reason: String,
    val permission: String,
    val result: String,
    val verification: String,
    val status: String // "SUCCESS", "REJECTED", "FAILED", "BLOCKED"
)

@Entity(tableName = "security_events")
data class SecurityEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String,
    val severity: String, // "LOW", "MEDIUM", "HIGH", "CRITICAL"
    val details: String,
    val isResolved: Boolean = false
)

@Entity(tableName = "knowledge_documents")
data class DocumentKnowledgeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,
    val content: String,
    val chunkCount: Int = 1,
    val sourceUrl: String = "",
    val tags: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val userId: String = "primary_user",
    val name: String = "Sir",
    val email: String = "user@manisk.os",
    val autonomyLevel: Int = 3, // Default Level 3: Execute Approved
    val activeProvider: String = "NEMOTRON",
    val biometricEnabled: Boolean = true,
    val emergencyLockdown: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
