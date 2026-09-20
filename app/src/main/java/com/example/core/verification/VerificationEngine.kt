package com.example.core.verification

import com.example.core.models.PlanStep
import com.example.core.models.VerificationResult

interface VerificationValidator {
    suspend fun preCheck(step: PlanStep): Result<Unit>
    suspend fun postCheck(step: PlanStep, executionOutput: String): VerificationResult
}

class VerificationEngine {

    suspend fun runPreCheck(step: PlanStep, validators: Map<String, VerificationValidator>): Result<Unit> {
        val validator = validators[step.toolName]
        if (validator != null) {
            val preCheckResult = validator.preCheck(step)
            if (preCheckResult.isFailure) {
                return preCheckResult
            }
        }

        // Core generic parameter checks
        if (step.description.isBlank()) {
            return Result.failure(IllegalArgumentException("Step description cannot be blank"))
        }

        return Result.success(Unit)
    }

    suspend fun runPostCheck(
        step: PlanStep,
        executionOutput: String,
        validators: Map<String, VerificationValidator>,
        maxRetries: Int = 2
    ): VerificationResult {
        val validator = validators[step.toolName]
        if (validator != null) {
            return validator.postCheck(step, executionOutput)
        }

        // Generic post-check verification
        val isExplicitSuccess = executionOutput.contains("SUCCESS", ignoreCase = true) ||
                executionOutput.contains("created", ignoreCase = true) ||
                executionOutput.contains("updated", ignoreCase = true) ||
                executionOutput.contains("verified", ignoreCase = true)

        return VerificationResult(
            success = isExplicitSuccess,
            message = if (isExplicitSuccess) "Operation outcome matched expectation." else "Unable to verify outcome evidence.",
            expectedState = step.expectedOutcome,
            actualState = executionOutput,
            evidence = "Telemetry verified: $executionOutput",
            retryCount = 0
        )
    }
}
