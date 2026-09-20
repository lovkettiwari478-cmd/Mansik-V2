package com.example.core.orchestrator

import com.example.agents.AgentManager
import com.example.core.context.ContextEngine
import com.example.core.emergency.EmergencyController
import com.example.core.firewall.PermissionDecision
import com.example.core.firewall.PermissionFirewall
import com.example.core.modelrouter.ModelRouter
import com.example.core.models.AgentTelemetry
import com.example.core.models.AutonomyLevel
import com.example.core.models.ChatMessage
import com.example.core.models.ExecutionPlan
import com.example.core.models.PlanStep
import com.example.core.models.TaskRoutingCategory
import com.example.core.models.VerificationResult
import com.example.core.planner.Planner
import com.example.core.verification.VerificationEngine
import com.example.database.AuditDao
import com.example.database.AuditLogEntity
import com.example.tools.ToolRegistry
import kotlinx.coroutines.delay
import java.util.UUID

data class OrchestratorExecutionState(
    val chatMessage: ChatMessage,
    val pendingStepForConfirmation: PlanStep? = null
)

class Orchestrator(
    private val modelRouter: ModelRouter,
    private val planner: Planner,
    private val permissionFirewall: PermissionFirewall,
    private val verificationEngine: VerificationEngine,
    private val toolRegistry: ToolRegistry,
    private val agentManager: AgentManager,
    private val contextEngine: ContextEngine,
    private val emergencyController: EmergencyController,
    private val auditDao: AuditDao
) {
    suspend fun processUserIntent(
        userIntent: String,
        autonomyLevel: AutonomyLevel,
        onTelemetryUpdate: suspend (AgentTelemetry) -> Unit
    ): OrchestratorExecutionState {
        val telemetry = mutableListOf<AgentTelemetry>()

        fun emitTelemetry(stepName: String, detail: String, status: String): AgentTelemetry {
            val item = AgentTelemetry(stepName, detail, status)
            telemetry.add(item)
            return item
        }

        // STEP 0: Check Emergency Kill Switch
        if (emergencyController.isEmergencyStopActive.value) {
            val tel = emitTelemetry("EMERGENCY_LOCK", "All agents halted by Emergency Stop command.", "BLOCKED")
            onTelemetryUpdate(tel)
            return OrchestratorExecutionState(
                ChatMessage(
                    id = UUID.randomUUID().toString(),
                    sender = "MANISK",
                    content = "🛑 EMERGENCY HALT ACTIVE: All autonomous operations and agents are locked down. Lift emergency lockdown in the Security Center to resume.",
                    telemetry = telemetry
                )
            )
        }

        // STEP 1: UNDERSTAND
        val telUnderstand = emitTelemetry("UNDERSTAND", "Analyzing user goal semantics and risk profile...", "ANALYZING")
        onTelemetryUpdate(telUnderstand)

        // STEP 2: GATHER CONTEXT
        val currentContext = contextEngine.assembleCurrentContext(autonomyLevel, modelRouter.currentProvider)
        val telContext = emitTelemetry(
            "CONTEXT",
            "Retrieved system context: ${currentContext.activeTasksCount} pending tasks, ${currentContext.connectedDevicesCount} devices online.",
            "CONTEXT_GATHERED"
        )
        onTelemetryUpdate(telContext)

        // STEP 3: PLAN
        val plan = planner.generatePlanForGoal(userIntent)
        val telPlan = emitTelemetry(
            "PLAN",
            "Generated ${plan.steps.size} structured steps with ${plan.constraints.size} constraints. Overall risk: ${plan.riskSummary.label}.",
            "PLANNING"
        )
        onTelemetryUpdate(telPlan)

        // If Autonomy is Level 0, we only provide a textual answer without running tools
        if (autonomyLevel == AutonomyLevel.LEVEL_0) {
            val responseText = modelRouter.routeAndComplete(userIntent, category = TaskRoutingCategory.FAST)
            return OrchestratorExecutionState(
                ChatMessage(
                    id = UUID.randomUUID().toString(),
                    sender = "MANISK",
                    content = "$responseText\n\n(Autonomy Level 0 active: No tools invoked)",
                    telemetry = telemetry,
                    plan = plan
                )
            )
        }

        // STEP 4: SIMULATE / VALIDATE & CHECK PERMISSIONS FOR EACH STEP
        val executedSteps = mutableListOf<String>()
        var pendingConfirmationStep: PlanStep? = null
        var allVerified = true
        val verificationProofs = mutableListOf<String>()

        for (step in plan.steps) {
            val telCheck = emitTelemetry(
                "CHECK_PERMISSIONS",
                "Evaluating step ${step.stepIndex}: [${step.agentName}] -> ${step.toolName} requires ${step.requiredPermission.key}",
                "AWAITING_PERM"
            )
            onTelemetryUpdate(telCheck)

            val permDecision = permissionFirewall.checkPermission(step.requiredPermission, autonomyLevel)

            when (permDecision) {
                is PermissionDecision.Granted -> {
                    // Proceed to EXECUTE and VERIFY
                    val telExec = emitTelemetry("EXECUTE", "Executing approved tool: ${step.toolName}", "EXECUTING")
                    onTelemetryUpdate(telExec)

                    val tool = toolRegistry.getTool(step.toolName)
                    if (tool != null) {
                        val result = tool.execute(step.arguments)
                        step.isExecuted = true
                        step.executionResult = result.output

                        // POST-CHECK VERIFICATION
                        val telVerif = emitTelemetry("VERIFY_RESULT", "Verifying outcome against ground truth...", "VERIFYING")
                        onTelemetryUpdate(telVerif)

                        val verifResult = verificationEngine.runPostCheck(step, result.output, emptyMap())
                        step.isVerified = verifResult.success
                        step.verificationStatus = verifResult.message

                        if (verifResult.success) {
                            executedSteps.add("• [${step.agentName}] ${step.description}: Verified ✓")
                            verificationProofs.add(verifResult.evidence)
                        } else {
                            allVerified = false
                            executedSteps.add("• [${step.agentName}] ${step.description}: Verification Failed ✗ (${verifResult.message})")
                        }
                    } else {
                        executedSteps.add("• ${step.description}: Tool not configured.")
                    }
                }

                is PermissionDecision.RequiresConfirmation -> {
                    // Stop execution pipeline and request interactive user authorization
                    pendingConfirmationStep = step
                    val telPause = emitTelemetry(
                        "PERMISSION_REQUIRED",
                        "Step requires explicit authorization: ${step.requiredPermission.displayName} (${permDecision.risk.label})",
                        "PAUSED_FOR_USER"
                    )
                    onTelemetryUpdate(telPause)
                    break
                }

                is PermissionDecision.Denied -> {
                    step.isExecuted = false
                    step.executionResult = "Permission Denied: ${permDecision.reason}"
                    executedSteps.add("• [${step.agentName}] Blocked: ${permDecision.reason}")
                }

                is PermissionDecision.EmergencyBlocked -> {
                    step.isExecuted = false
                    executedSteps.add("• Operation halted: Emergency lockdown active.")
                    break
                }
            }
        }

        // STEP 5: REPORT & STORE RELEVANT MEMORY
        val responseBuilder = StringBuilder()

        if (pendingConfirmationStep != null) {
            responseBuilder.append("I have formulated an execution plan for your goal, but step ${pendingConfirmationStep.stepIndex} requires your explicit permission.\n\n")
            responseBuilder.append("Action: ${pendingConfirmationStep.description}\n")
            responseBuilder.append("Permission: ${pendingConfirmationStep.requiredPermission.displayName} (${pendingConfirmationStep.requiredPermission.risk.label})\n\n")
            responseBuilder.append("Please authorize or deny this step below.")
        } else if (executedSteps.isNotEmpty()) {
            responseBuilder.append("Execution complete:\n\n")
            executedSteps.forEach { responseBuilder.append("$it\n") }

            if (allVerified) {
                responseBuilder.append("\nVerification: Successful. Ground truth states confirmed in local storage.")
            } else {
                responseBuilder.append("\nVerification: Warning — Some actions could not be fully verified.")
            }
        } else {
            responseBuilder.append(modelRouter.routeAndComplete(userIntent, category = TaskRoutingCategory.REASONING))
        }

        val chatMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = "MANISK",
            content = responseBuilder.toString().trim(),
            telemetry = telemetry,
            plan = plan,
            pendingPermissionStep = pendingConfirmationStep,
            isVerified = allVerified && executedSteps.isNotEmpty() && pendingConfirmationStep == null,
            verificationDetail = if (verificationProofs.isNotEmpty()) verificationProofs.joinToString("; ") else null
        )

        return OrchestratorExecutionState(
            chatMessage = chatMessage,
            pendingStepForConfirmation = pendingConfirmationStep
        )
    }

    suspend fun executeConfirmedStep(
        step: PlanStep,
        autonomyLevel: AutonomyLevel
    ): PlanStep {
        // Mark approved
        step.isApproved = true
        val tool = toolRegistry.getTool(step.toolName)
        if (tool != null) {
            val result = tool.execute(step.arguments)
            step.isExecuted = true
            step.executionResult = result.output

            val verif = verificationEngine.runPostCheck(step, result.output, emptyMap())
            step.isVerified = verif.success
            step.verificationStatus = verif.message
        }
        return step
    }
}
