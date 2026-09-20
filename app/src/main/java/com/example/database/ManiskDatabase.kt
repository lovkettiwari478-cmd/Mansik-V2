package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.core.models.GrantType
import com.example.core.models.PermissionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TaskEntity::class,
        CalendarEventEntity::class,
        MemoryEntity::class,
        PermissionEntity::class,
        AutomationRuleEntity::class,
        SmartDeviceEntity::class,
        AuditLogEntity::class,
        SecurityEventEntity::class,
        DocumentKnowledgeEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ManiskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun calendarDao(): CalendarDao
    abstract fun memoryDao(): MemoryDao
    abstract fun permissionDao(): PermissionDao
    abstract fun automationDao(): AutomationDao
    abstract fun smartDeviceDao(): SmartDeviceDao
    abstract fun auditDao(): AuditDao
    abstract fun securityDao(): SecurityDao
    abstract fun knowledgeDao(): KnowledgeDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: ManiskDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ManiskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ManiskDatabase::class.java,
                    "manisk_os.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(database: ManiskDatabase) {
                // 1. Initial User Profile
                database.userDao().insertOrUpdateProfile(
                    UserProfileEntity(
                        userId = "primary_user",
                        name = "Anita",
                        email = "anita94067@gmail.com",
                        autonomyLevel = 3,
                        activeProvider = "NEMOTRON",
                        biometricEnabled = true,
                        emergencyLockdown = false
                    )
                )

                // 2. Initial Permissions Table
                PermissionType.values().forEach { perm ->
                    val defaultGranted = when (perm) {
                        PermissionType.CALENDAR_READ,
                        PermissionType.SMART_HOME_READ,
                        PermissionType.MEMORY_STORE,
                        PermissionType.LOCATION_USE -> true
                        PermissionType.CALENDAR_WRITE,
                        PermissionType.SMART_HOME_CONTROL,
                        PermissionType.EMAIL_DRAFT -> true
                        else -> false
                    }
                    val grantType = if (defaultGranted) GrantType.ALLOW.name else GrantType.ASK_EVERY_TIME.name
                    database.permissionDao().insertOrUpdate(
                        PermissionEntity(
                            permissionKey = perm.key,
                            displayName = perm.displayName,
                            riskLevel = perm.risk.name,
                            isGranted = defaultGranted,
                            grantType = grantType,
                            grantedUntil = 0L
                        )
                    )
                }

                // 3. Initial Smart Devices
                val devices = listOf(
                    SmartDeviceEntity("ac_living", "Living Room AC", "AC", "Living Room", "{\"isOn\":false,\"targetTemp\":22,\"mode\":\"COOL\"}"),
                    SmartDeviceEntity("light_living", "Living Ceiling Lights", "LIGHT", "Living Room", "{\"isOn\":false,\"brightness\":80,\"colorTemp\":\"WARM\"}"),
                    SmartDeviceEntity("fan_master", "Master Bedroom Fan", "FAN", "Bedroom", "{\"isOn\":false,\"speed\":2}"),
                    SmartDeviceEntity("tv_living", "Sony Bravia OLED", "TV", "Living Room", "{\"isOn\":false,\"input\":\"HDMI 1\"}"),
                    SmartDeviceEntity("thermo_nest", "Nest Climate Sensor", "THERMOSTAT", "Hallway", "{\"currentTemp\":24.2,\"humidity\":48}")
                )
                devices.forEach { database.smartDeviceDao().insertDevice(it) }

                // 4. Initial Automations
                val automations = listOf(
                    AutomationRuleEntity(
                        name = "Welcome Home Protocol",
                        mode = "WELCOME_HOME",
                        triggerType = "LOCATION_ARRIVAL",
                        conditionsJson = "{\"identityVerified\":true,\"timeAfter\":18}",
                        actionsJson = "{\"ac\":true,\"temp\":22,\"lights\":true,\"brightness\":70,\"greet\":true}",
                        isEnabled = true
                    ),
                    AutomationRuleEntity(
                        name = "Meeting Focus Protocol",
                        mode = "MEETING",
                        triggerType = "CALENDAR_APPROACHING",
                        conditionsJson = "{\"minutesBefore\":10,\"atDesk\":true}",
                        actionsJson = "{\"muteNotifications\":true,\"lights\":true,\"brightness\":100,\"prepareBrief\":true}",
                        isEnabled = true
                    ),
                    AutomationRuleEntity(
                        name = "Night Sanctuary Mode",
                        mode = "SLEEP",
                        triggerType = "TIME",
                        conditionsJson = "{\"time\":\"23:00\"}",
                        actionsJson = "{\"lights\":false,\"ac\":true,\"temp\":21,\"lockDoors\":true}",
                        isEnabled = true
                    )
                )
                automations.forEach { database.automationDao().insertRule(it) }

                // 5. Initial Sample Calendar Events
                val now = System.currentTimeMillis()
                val oneHour = 3600 * 1000L
                val events = listOf(
                    CalendarEventEntity(
                        title = "Q4 Product Strategy & Roadmap Meeting",
                        description = "Key stakeholder alignment with Architecture, DevOps, and Product leadership.",
                        startTime = now + (3 * oneHour),
                        endTime = now + (4 * oneHour),
                        location = "Google Meet (Room Alpha)",
                        isVerified = true
                    ),
                    CalendarEventEntity(
                        title = "DevOps Infrastructure Review",
                        description = "Verify zero-trust permissions, encrypted secret lifecycle, and pipeline observability.",
                        startTime = now + (26 * oneHour),
                        endTime = now + (27 * oneHour),
                        location = "War Room 4",
                        isVerified = true
                    )
                )
                events.forEach { database.calendarDao().insertEvent(it) }

                // 6. Initial Tasks
                val tasks = listOf(
                    TaskEntity(
                        title = "Review Security Firewall Matrix",
                        description = "Ensure least-privilege principle is enforced across all 13 specialized agents.",
                        priority = "HIGH",
                        deadline = now + (12 * oneHour),
                        status = "TODO",
                        project = "Security Core",
                        estimatedDuration = "45m"
                    ),
                    TaskEntity(
                        title = "Prepare Q4 Meeting Executive Summary",
                        description = "Collate system uptime, verification rates, and autonomous efficiency metrics.",
                        priority = "URGENT",
                        deadline = now + (2 * oneHour),
                        status = "IN_PROGRESS",
                        project = "Product",
                        estimatedDuration = "30m"
                    )
                )
                tasks.forEach { database.taskDao().insertTask(it) }

                // 7. Initial Memory Items
                val memories = listOf(
                    MemoryEntity(
                        content = "User prefers concise executive summaries with explicit verification evidence before closing tasks.",
                        type = "PREFERENCES",
                        source = "User Conversation",
                        confidence = 0.98f,
                        sensitivity = "NORMAL"
                    ),
                    MemoryEntity(
                        content = "Project MANISK is designed as a Personal AI Operating System rather than a basic chatbot.",
                        type = "PROJECTS",
                        source = "System Initialization",
                        confidence = 1.0f,
                        sensitivity = "NORMAL"
                    )
                )
                memories.forEach { database.memoryDao().insertMemory(it) }

                // 8. Initial Audit Record
                database.auditDao().insertLog(
                    AuditLogEntity(
                        timestamp = now,
                        agent = "SecurityAgent",
                        tool = "system.bootstrap",
                        action = "Initialize Secure Core & Permission Firewall",
                        reason = "MANISK Boot Protocol",
                        permission = "system.admin",
                        result = "Success",
                        verification = "All 13 agents registered with strict permission boundaries",
                        status = "SUCCESS"
                    )
                )
            }
        }
    }
}
