package com.example.core.emergency

import com.example.core.firewall.PermissionFirewall
import com.example.database.AuditDao
import com.example.database.AuditLogEntity
import com.example.database.AutomationDao
import com.example.database.UserDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EmergencyController(
    private val permissionFirewall: PermissionFirewall,
    private val automationDao: AutomationDao,
    private val userDao: UserDao,
    private val auditDao: AuditDao
) {
    private val _isEmergencyStopActive = MutableStateFlow(false)
    val isEmergencyStopActive: StateFlow<Boolean> = _isEmergencyStopActive.asStateFlow()

    suspend fun triggerEmergencyStop(reason: String = "User manual trigger: STOP ALL AGENTS") {
        _isEmergencyStopActive.value = true
        permissionFirewall.isEmergencyLockdown = true

        // Disable all automations
        automationDao.disableAllAutomations()

        // Set user profile lockdown
        userDao.setEmergencyLockdown("primary_user", true)

        // Clear active session grants
        permissionFirewall.clearSessionGrants()

        // Record high-priority audit log
        auditDao.insertLog(
            AuditLogEntity(
                timestamp = System.currentTimeMillis(),
                agent = "SecurityAgent",
                tool = "emergency.stop_all",
                action = "EMERGENCY SHUTDOWN TRIGGERED",
                reason = reason,
                permission = "security.emergency",
                result = "Active agents halted, automations paused, permissions locked",
                verification = "isEmergencyStopActive = true",
                status = "SUCCESS"
            )
        )
    }

    suspend fun resumeNormalOperations() {
        _isEmergencyStopActive.value = false
        permissionFirewall.isEmergencyLockdown = false
        userDao.setEmergencyLockdown("primary_user", false)

        auditDao.insertLog(
            AuditLogEntity(
                timestamp = System.currentTimeMillis(),
                agent = "SecurityAgent",
                tool = "emergency.resume",
                action = "Emergency lockdown lifted",
                reason = "User explicit resume",
                permission = "security.emergency",
                result = "System returned to standard operation",
                verification = "isEmergencyStopActive = false",
                status = "SUCCESS"
            )
        )
    }

    suspend fun revokeAllPermissions() {
        permissionFirewall.revokeAll()
        auditDao.insertLog(
            AuditLogEntity(
                timestamp = System.currentTimeMillis(),
                agent = "SecurityAgent",
                tool = "permissions.revoke_all",
                action = "Revoked all granted permissions",
                reason = "User manual wipe",
                permission = "security.emergency",
                result = "All permission entries marked REVOKED",
                verification = "Database zero-trust confirmed",
                status = "SUCCESS"
            )
        )
    }
}
