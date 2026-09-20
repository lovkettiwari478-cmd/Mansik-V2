package com.example.core.planner

import com.example.core.models.ExecutionPlan
import com.example.core.models.PermissionType
import com.example.core.models.PlanStep
import com.example.core.models.RiskLevel
import java.util.UUID

class Planner {

    fun generatePlanForGoal(goal: String): ExecutionPlan {
        val lower = goal.lowercase()
        val steps = mutableListOf<PlanStep>()
        val constraints = mutableListOf<String>()

        var maxRisk = RiskLevel.LOW

        when {
            lower.contains("meeting") || lower.contains("prepare everything for my meeting") -> {
                constraints.add("Strict calendar conflict check")
                constraints.add("Draft only for external messages — no unsolicited sending")
                constraints.add("Verify all tasks created in database")

                steps.add(
                    PlanStep(
                        id = UUID.randomUUID().toString(),
                        stepIndex = 1,
                        agentName = "CalendarAgent",
                        toolName = "calendar.query",
                        description = "Query calendar for tomorrow's scheduled meetings",
                        requiredPermission = PermissionType.CALENDAR_READ,
                        expectedOutcome = "Target meeting identified and time slot verified"
                    )
                )
                steps.add(
                    PlanStep(
                        id = UUID.randomUUID().toString(),
                        stepIndex = 2,
                        agentName = "TaskAgent",
                        toolName = "tasks.create_task",
                        description = "Create task: 'Review Q4 Meeting Agenda & Deck'",
                        requiredPermission = PermissionType.MEMORY_STORE,
                        arguments = mapOf(
                            "title" to "Review Q4 Meeting Agenda & Deck",
                            "priority" to "HIGH",
                            "project" to "Leadership Meeting"
                        ),
                        dependencies = listOf("1"),
                        expectedOutcome = "Task stored in Room DB with verified ID"
                    )
                )
                steps.add(
                    PlanStep(
                        id = UUID.randomUUID().toString(),
                        stepIndex = 3,
                        agentName = "CommunicationAgent",
                        toolName = "communication.draft",
                        description = "Draft briefing notes for attendees (Review required before send)",
                        requiredPermission = PermissionType.EMAIL_DRAFT,
                        arguments = mapOf(
                            "recipient" to "team@manisk.org",
                            "subject" to "Preparation briefing for meeting tomorrow"
                        ),
                        dependencies = listOf("2"),
                        expectedOutcome = "Draft recorded for user approval"
                    )
                )
                steps.add(
                    PlanStep(
                        id = UUID.randomUUID().toString(),
                        stepIndex = 4,
                        agentName = "SmartHomeAgent",
                        toolName = "smarthome.set_state",
                        description = "Configure Living Room AC (22°C) and optimal lighting for prep",
                        requiredPermission = PermissionType.SMART_HOME_CONTROL,
                        arguments = mapOf(
                            "deviceId" to "ac_living",
                            "stateJson" to "{\"isOn\":true,\"targetTemp\":22,\"mode\":\"COOL\"}"
                        ),
                        expectedOutcome = "Living Room AC turned ON at 22°C"
                    )
                )
                maxRisk = RiskLevel.MEDIUM
            }

            lower.contains("exam") || lower.contains("study") -> {
                constraints.add("Zero calendar schedule overlaps")
                constraints.add("Sequential milestone dependencies")
                constraints.add("Study duration must fit before Friday deadline")

                steps.add(
                    PlanStep(
                        id = UUID.randomUUID().toString(),
                        stepIndex = 1,
                        agentName = "CalendarAgent",
                        toolName = "calendar.query",
                        description = "Check calendar for study slots and exam day Friday",
                        requiredPermission = PermissionType.CALENDAR_READ,
                        expectedOutcome = "Time windows available and verified"
                    )
                )
                steps.add(
                    PlanStep(
                        id = UUID.randomUUID().toString(),
                        stepIndex = 2,
                        agentName = "TaskAgent",
                        toolName = "tasks.create_task",
                        description = "Milestone 1: Complete High-Yield Concept Review",
                        requiredPermission = PermissionType.MEMORY_STORE,
                        arguments = mapOf(
                            "title" to "High-Yield Concept Review",
                            "priority" to "HIGH",
                            "project" to "Exam Prep",
                            "estimatedDuration" to "2h"
                        ),
                        expectedOutcome = "Milestone 1 created and verified"
                    )
                )
                steps.add(
                    PlanStep(
                        id = UUID.randomUUID().toString(),
                        stepIndex = 3,
                        agentName = "TaskAgent",
                        toolName = "tasks.create_task",
                        description = "Milestone 2: Timed Mock Practice Exam",
                        requiredPermission = PermissionType.MEMORY_STORE,
                        arguments = mapOf(
                            "title" to "Timed Mock Practice Exam",
                            "priority" to "URGENT",
                            "project" to "Exam Prep",
                            "estimatedDuration" to "90m"
                        ),
                        dependencies = listOf("2"),
                        expectedOutcome = "Milestone 2 created and verified"
                    )
                )
                steps.add(
                    PlanStep(
                        id = UUID.randomUUID().toString(),
                        stepIndex = 4,
                        agentName = "CalendarAgent",
                        toolName = "calendar.create_event",
                        description = "Block 3-hour Focus Study block on Thursday evening",
                        requiredPermission = PermissionType.CALENDAR_WRITE,
                        arguments = mapOf(
                            "title" to "Deep Study Focus Block (Pre-Exam)",
                            "startOffsetHours" to 20.0,
                            "durationHours" to 3.0,
                            "location" to "Study Room"
                        ),
                        dependencies = listOf("3"),
                        expectedOutcome = "Calendar block confirmed and verified"
                    )
                )
                maxRisk = RiskLevel.MEDIUM
            }

            lower.contains("welcome home") || (lower.contains("ac") && lower.contains("turn on")) -> {
                constraints.add("Authorized identity required")
                constraints.add("Safe temperature bounds: 20-26°C")

                steps.add(
                    PlanStep(
                        id = UUID.randomUUID().toString(),
                        stepIndex = 1,
                        agentName = "SmartHomeAgent",
                        toolName = "smarthome.set_state",
                        description = "Power ON Living Room AC (22°C Cool)",
                        requiredPermission = PermissionType.SMART_HOME_CONTROL,
                        arguments = mapOf(
                            "deviceId" to "ac_living",
                            "stateJson" to "{\"isOn\":true,\"targetTemp\":22,\"mode\":\"COOL\"}"
                        ),
                        expectedOutcome = "AC state = ON, 22°C"
                    )
                )
                steps.add(
                    PlanStep(
                        id = UUID.randomUUID().toString(),
                        stepIndex = 2,
                        agentName = "SmartHomeAgent",
                        toolName = "smarthome.set_state",
                        description = "Turn ON Warm Welcome Lighting (75% Brightness)",
                        requiredPermission = PermissionType.SMART_HOME_CONTROL,
                        arguments = mapOf(
                            "deviceId" to "light_living",
                            "stateJson" to "{\"isOn\":true,\"brightness\":75,\"colorTemp\":\"WARM\"}"
                        ),
                        expectedOutcome = "Living Lights state = ON"
                    )
                )
                maxRisk = RiskLevel.MEDIUM
            }

            lower.startsWith("remember") -> {
                val cleanContent = goal.removePrefix("remember").removePrefix("Remember").trim()
                steps.add(
                    PlanStep(
                        id = UUID.randomUUID().toString(),
                        stepIndex = 1,
                        agentName = "MemoryAgent",
                        toolName = "memory.remember",
                        description = "Persist user memory in Room Knowledge Store",
                        requiredPermission = PermissionType.MEMORY_STORE,
                        arguments = mapOf("content" to cleanContent, "category" to "FACTS"),
                        expectedOutcome = "Memory saved and retrievable"
                    )
                )
                maxRisk = RiskLevel.LOW
            }

            lower.startsWith("forget") -> {
                val cleanContent = goal.removePrefix("forget").removePrefix("Forget").trim()
                steps.add(
                    PlanStep(
                        id = UUID.randomUUID().toString(),
                        stepIndex = 1,
                        agentName = "MemoryAgent",
                        toolName = "memory.forget",
                        description = "Purge memories matching query: '$cleanContent'",
                        requiredPermission = PermissionType.MEMORY_DELETE,
                        arguments = mapOf("query" to cleanContent),
                        expectedOutcome = "Memory purged from store"
                    )
                )
                maxRisk = RiskLevel.MEDIUM
            }

            else -> {
                // Generalized task decomposition
                steps.add(
                    PlanStep(
                        id = UUID.randomUUID().toString(),
                        stepIndex = 1,
                        agentName = "TaskAgent",
                        toolName = "tasks.create_task",
                        description = "Record goal action item: '$goal'",
                        requiredPermission = PermissionType.MEMORY_STORE,
                        arguments = mapOf("title" to goal, "priority" to "MEDIUM"),
                        expectedOutcome = "Task logged with verified ID"
                    )
                )
                maxRisk = RiskLevel.LOW
            }
        }

        return ExecutionPlan(
            goal = goal,
            constraints = constraints,
            steps = steps,
            riskSummary = maxRisk,
            requiresExplicitApproval = maxRisk == RiskLevel.HIGH || maxRisk == RiskLevel.CRITICAL
        )
    }
}
