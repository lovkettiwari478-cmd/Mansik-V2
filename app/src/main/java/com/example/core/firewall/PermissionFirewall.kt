package com.example.core.firewall

import com.example.core.models.AutonomyLevel
import com.example.core.models.GrantType
import com.example.core.models.PermissionType
import com.example.core.models.RiskLevel
import com.example.database.PermissionDao
import kotlinx.coroutines.flow.firstOrNull

sealed class PermissionDecision {
    data object Granted : PermissionDecision()
    data class RequiresConfirmation(val permission: PermissionType, val risk: RiskLevel, val reason: String) : PermissionDecision()
    data class Denied(val permission: PermissionType, val reason: String) : PermissionDecision()
    data object EmergencyBlocked : PermissionDecision()
}

class PermissionFirewall(
    private val permissionDao: PermissionDao
) {
    @Volatile
    var isEmergencyLockdown: Boolean = false

    private val sessionGrants = mutableSetOf<String>()

    fun grantForSession(permissionKey: String) {
        sessionGrants.add(permissionKey)
    }

    fun clearSessionGrants() {
        sessionGrants.clear()
    }

    suspend fun checkPermission(
        permission: PermissionType,
        autonomyLevel: AutonomyLevel,
        isAutomatedBackgroundAction: Boolean = false
    ): PermissionDecision {
        if (isEmergencyLockdown) {
            return PermissionDecision.EmergencyBlocked
        }

        // Check in-memory session grant
        if (sessionGrants.contains(permission.key)) {
            return PermissionDecision.Granted
        }

        val entity = permissionDao.getPermission(permission.key)
        val isGranted = entity?.isGranted ?: false
        val grantType = try {
            GrantType.valueOf(entity?.grantType ?: GrantType.ASK_EVERY_TIME.name)
        } catch (e: Exception) {
            GrantType.ASK_EVERY_TIME
        }

        // Timestamp expiration check
        if (grantType == GrantType.UNTIL_TIMESTAMP && entity != null && entity.grantedUntil > 0) {
            if (System.currentTimeMillis() > entity.grantedUntil) {
                return PermissionDecision.RequiresConfirmation(
                    permission,
                    permission.risk,
                    "Temporary permission expired."
                )
            }
        }

        // Explicitly denied or revoked
        if (grantType == GrantType.DENY || grantType == GrantType.REVOKED) {
            return PermissionDecision.Denied(permission, "Permission explicitly blocked or revoked by user policy.")
        }

        // Autonomy Level rules:
        // Level 0: Tool execution never allowed
        if (autonomyLevel == AutonomyLevel.LEVEL_0) {
            return PermissionDecision.Denied(permission, "Autonomy Level 0 allows answer-only interaction.")
        }

        // Level 1: Suggest only
        if (autonomyLevel == AutonomyLevel.LEVEL_1) {
            return PermissionDecision.RequiresConfirmation(
                permission,
                permission.risk,
                "Autonomy Level 1 requires confirmation for all actions."
            )
        }

        // Level 2: Ask before action
        if (autonomyLevel == AutonomyLevel.LEVEL_2) {
            return PermissionDecision.RequiresConfirmation(
                permission,
                permission.risk,
                "Autonomy Level 2 asks before executing any tool."
            )
        }

        // Level 3 & Level 4:
        // Critical and High risk always require explicit confirmation unless already granted for session or explicit ALLOW
        if (permission.risk == RiskLevel.CRITICAL) {
            return PermissionDecision.RequiresConfirmation(
                permission,
                permission.risk,
                "Critical-risk action requires interactive authorization."
            )
        }

        if (permission.risk == RiskLevel.HIGH && grantType != GrantType.ALLOW) {
            return PermissionDecision.RequiresConfirmation(
                permission,
                permission.risk,
                "High-risk action requires confirmation."
            )
        }

        if (grantType == GrantType.ASK_EVERY_TIME) {
            return PermissionDecision.RequiresConfirmation(
                permission,
                permission.risk,
                "Permission policy is set to Ask Every Time."
            )
        }

        if (isGranted || grantType == GrantType.ALLOW) {
            return PermissionDecision.Granted
        }

        return PermissionDecision.RequiresConfirmation(
            permission,
            permission.risk,
            "Permission not explicitly pre-approved."
        )
    }

    suspend fun updateGrant(key: String, isGranted: Boolean, grantType: GrantType, durationMs: Long = 0L) {
        val until = if (durationMs > 0) System.currentTimeMillis() + durationMs else 0L
        permissionDao.updateGrant(key, isGranted, grantType.name, until)
    }

    suspend fun revokeAll() {
        sessionGrants.clear()
        permissionDao.revokeAllPermissions()
    }
}
