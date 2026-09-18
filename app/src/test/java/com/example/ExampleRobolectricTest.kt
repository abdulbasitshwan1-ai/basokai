package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ai.ActionType
import com.example.ai.KurdishNlpEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
    assertEquals("BASOKA", appName)
  }

  @Test
  fun `test kurdish nlp timer extraction`() {
    val intent = KurdishNlpEngine.parseUserInput("تایمەرێکی 10 خولەکی دابنێ")
    assertEquals(ActionType.START_TIMER, intent.actionType)
    assertEquals(600, intent.timerDurationSeconds)
  }

  @Test
  fun `test kurdish nlp reminder extraction`() {
    val intent = KurdishNlpEngine.parseUserInput("سبەی کاتژمێر ٨ی بەیانی بیرم بخەرەوە پەیوەندی بکەم")
    assertEquals(ActionType.SET_REMINDER, intent.actionType)
    assertNotNull(intent.targetTimeMillis)
  }

  @Test
  fun `test kurdish nlp image generation extraction`() {
    val intent = KurdishNlpEngine.parseUserInput("وێنەی قەڵای هەولێر لە کاتی خۆرئاوابوون دروست بکە")
    assertEquals(ActionType.GENERATE_IMAGE, intent.actionType)
    assertEquals(com.example.data.model.AgentType.VISION, intent.agentType)
  }
}

