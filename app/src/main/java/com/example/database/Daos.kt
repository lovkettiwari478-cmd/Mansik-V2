package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status != 'COMPLETED' AND status != 'CANCELLED' ORDER BY deadline ASC")
    fun getPendingTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("UPDATE tasks SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTask(id: Long)
}

@Dao
interface CalendarDao {
    @Query("SELECT * FROM calendar_events ORDER BY startTime ASC")
    fun getAllEvents(): Flow<List<CalendarEventEntity>>

    @Query("SELECT * FROM calendar_events WHERE startTime >= :now ORDER BY startTime ASC LIMIT 5")
    fun getUpcomingEvents(now: Long): Flow<List<CalendarEventEntity>>

    @Query("SELECT * FROM calendar_events WHERE id = :id")
    suspend fun getEventById(id: Long): CalendarEventEntity?

    @Query("SELECT * FROM calendar_events WHERE title LIKE '%' || :query || '%' LIMIT 1")
    suspend fun findEventByTitle(query: String): CalendarEventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CalendarEventEntity): Long

    @Query("DELETE FROM calendar_events WHERE id = :id")
    suspend fun deleteEvent(id: Long)
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories ORDER BY createdAt DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE type = :type ORDER BY createdAt DESC")
    fun getMemoriesByType(type: String): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE content LIKE '%' || :query || '%' ORDER BY confidence DESC")
    suspend fun searchMemories(query: String): List<MemoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity): Long

    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteMemory(id: Long)

    @Query("DELETE FROM memories WHERE content LIKE '%' || :query || '%'")
    suspend fun deleteMemoryByContent(query: String): Int
}

@Dao
interface PermissionDao {
    @Query("SELECT * FROM permissions ORDER BY permissionKey ASC")
    fun getAllPermissions(): Flow<List<PermissionEntity>>

    @Query("SELECT * FROM permissions WHERE permissionKey = :key LIMIT 1")
    suspend fun getPermission(key: String): PermissionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(permission: PermissionEntity)

    @Query("UPDATE permissions SET isGranted = :isGranted, grantType = :grantType, grantedUntil = :grantedUntil WHERE permissionKey = :key")
    suspend fun updateGrant(key: String, isGranted: Boolean, grantType: String, grantedUntil: Long)

    @Query("UPDATE permissions SET isGranted = 0, grantType = 'REVOKED'")
    suspend fun revokeAllPermissions()
}

@Dao
interface AutomationDao {
    @Query("SELECT * FROM automations ORDER BY id ASC")
    fun getAllRules(): Flow<List<AutomationRuleEntity>>

    @Query("SELECT * FROM automations WHERE isEnabled = 1")
    fun getEnabledRules(): Flow<List<AutomationRuleEntity>>

    @Query("SELECT * FROM automations WHERE mode = :mode LIMIT 1")
    suspend fun getRuleByMode(mode: String): AutomationRuleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: AutomationRuleEntity): Long

    @Query("UPDATE automations SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun setRuleEnabled(id: Long, isEnabled: Boolean)

    @Query("UPDATE automations SET lastTriggered = :timestamp WHERE id = :id")
    suspend fun updateLastTriggered(id: Long, timestamp: Long)

    @Query("UPDATE automations SET isEnabled = 0")
    suspend fun disableAllAutomations()
}

@Dao
interface SmartDeviceDao {
    @Query("SELECT * FROM smart_devices ORDER BY room ASC, name ASC")
    fun getAllDevices(): Flow<List<SmartDeviceEntity>>

    @Query("SELECT * FROM smart_devices WHERE deviceId = :deviceId LIMIT 1")
    suspend fun getDevice(deviceId: String): SmartDeviceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: SmartDeviceEntity)

    @Query("UPDATE smart_devices SET stateJson = :stateJson, lastUpdated = :timestamp WHERE deviceId = :deviceId")
    suspend fun updateDeviceState(deviceId: String, stateJson: String, timestamp: Long)
}

@Dao
interface AuditDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AuditLogEntity>>

    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 20")
    fun getRecentLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity): Long
}

@Dao
interface SecurityDao {
    @Query("SELECT * FROM security_events ORDER BY timestamp DESC")
    fun getAllEvents(): Flow<List<SecurityEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: SecurityEventEntity): Long

    @Query("UPDATE security_events SET isResolved = 1 WHERE id = :id")
    suspend fun resolveEvent(id: Long)
}

@Dao
interface KnowledgeDao {
    @Query("SELECT * FROM knowledge_documents ORDER BY createdAt DESC")
    fun getAllDocuments(): Flow<List<DocumentKnowledgeEntity>>

    @Query("SELECT * FROM knowledge_documents WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    suspend fun searchDocuments(query: String): List<DocumentKnowledgeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentKnowledgeEntity): Long
}

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE userId = :userId LIMIT 1")
    fun getProfile(userId: String = "primary_user"): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE userId = :userId LIMIT 1")
    suspend fun getProfileDirect(userId: String = "primary_user"): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET emergencyLockdown = :isLocked WHERE userId = :userId")
    suspend fun setEmergencyLockdown(userId: String = "primary_user", isLocked: Boolean)

    @Query("UPDATE user_profile SET autonomyLevel = :level WHERE userId = :userId")
    suspend fun setAutonomyLevel(userId: String = "primary_user", level: Int)

    @Query("UPDATE user_profile SET activeProvider = :provider WHERE userId = :userId")
    suspend fun setActiveProvider(userId: String = "primary_user", provider: String)
}
