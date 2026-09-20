package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.modelrouter.ModelRouter
import com.example.core.models.ModelProviderType
import com.example.core.models.RiskLevel
import com.example.core.planner.Planner
import com.example.core.verification.VerificationEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("MANISK", appName)
  }

  @Test
  fun `planner decomposes meeting preparation goal`() {
    val planner = Planner()
    val plan = planner.generatePlanForGoal("Prepare everything for my meeting tomorrow.")
    assertTrue(plan.steps.isNotEmpty())
    assertEquals(4, plan.steps.size)
    assertEquals(RiskLevel.MEDIUM, plan.riskSummary)
  }

  @Test
  fun `planner decomposes exam preparation goal`() {
    val planner = Planner()
    val plan = planner.generatePlanForGoal("I have an exam Friday. Help me prepare.")
    assertTrue(plan.steps.isNotEmpty())
    assertEquals(4, plan.steps.size)
  }

  @Test
  fun `model router switches providers dynamically`() {
    val router = ModelRouter(ModelProviderType.NEMOTRON)
    assertEquals(ModelProviderType.NEMOTRON, router.currentProvider)
    router.currentProvider = ModelProviderType.GEMINI
    assertEquals(ModelProviderType.GEMINI, router.currentProvider)
  }
}

