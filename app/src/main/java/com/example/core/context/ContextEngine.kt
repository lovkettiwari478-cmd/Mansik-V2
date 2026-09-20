package com.example.core.context

import com.example.core.firewall.PermissionFirewall
import com.example.core.models.AutonomyLevel
import com.example.core.models.ModelProviderType
import com.example.core.models.OSContext
import com.example.core.models.PermissionType
import com.example.database.CalendarDao
import com.example.database.SmartDeviceDao
import com.example.database.TaskDao
import com.example.database.UserDao
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ContextEngine(
    private val taskDao: TaskDao,
    private val calendarDao: CalendarDao,
    private val smartDeviceDao: SmartDeviceDao,
    private val userDao: UserDao,
    private val permissionFirewall: PermissionFirewall
) {
    suspend fun assembleCurrentContext(autonomyLevel: AutonomyLevel, providerType: ModelProviderType): OSContext {
        val now = System.currentTimeMillis()
        val timeFormat = SimpleDateFormat("EEEE, MMMM d, yyyy • h:mm a", Locale.getDefault())
        val timeStr = timeFormat.format(Date(now))

        val profile = userDao.getProfileDirect()
        val userName = profile?.name ?: "Sir"

        // Query pending tasks count
        val pendingTasks = taskDao.getPendingTasks().firstOrNull() ?: emptyList()
        val upcomingEvents = calendarDao.getUpcomingEvents(now).firstOrNull() ?: emptyList()
        val devices = smartDeviceDao.getAllDevices().firstOrNull() ?: emptyList()

        val secStatus = if (permissionFirewall.isEmergencyLockdown) {
            "EMERGENCY LOCKDOWN ACTIVE"
        } else {
            "Zero-Trust Guard: Operational"
        }

        return OSContext(
            currentTimeString = timeStr,
            userProfileName = userName,
            activeTasksCount = pendingTasks.size,
            upcomingEventsCount = upcomingEvents.size,
            activeAutomationMode = null,
            connectedDevicesCount = devices.size,
            securityStatus = secStatus,
            autonomyLevel = autonomyLevel,
            activeModelProvider = providerType
        )
    }
}
