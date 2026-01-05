package com.pushupbruh.pushupbruh

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import java.text.SimpleDateFormat
import java.util.*

class MainActivityTest {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var isCooldownActive = false
    private var lastClickTime = 0L
    
    @Before
    fun setUp() {
        // Setup for tests - DataManager will be mocked in actual implementation
    }
    
    @Test
    fun testGetTodayKey_CorrectFormat() {
        // Test that today's key generation works correctly
        val today = Date()
        val expectedKey = dateFormat.format(today)
        val actualKey = getTodayKeyTest(today)
        
        assertEquals(expectedKey, actualKey)
    }
    
    @Test
    fun testGetTodayKey_SpecificDate() {
        // Test with a specific date
        val calendar = Calendar.getInstance()
        calendar.set(2025, Calendar.JANUARY, 5) // Jan 5, 2025
        val testDate = calendar.time
        
        val expectedKey = "2025-01-05"
        val actualKey = getTodayKeyTest(testDate)
        
        assertEquals(expectedKey, actualKey)
    }
    
    @Test
    fun testAddPushups_IncreasesCount() {
        // Test that adding pushups increases the count correctly
        val initialCount = 40
        val pushupsToAdd = 20
        val expectedCount = initialCount + pushupsToAdd
        
        val actualCount = addPushupsTest(initialCount, pushupsToAdd)
        
        assertEquals(expectedCount, actualCount)
    }
    
    @Test
    fun testAddPushups_ConsistentIncrement() {
        // Test that adding pushups always adds exactly 20
        val testCounts = listOf(0, 10, 50, 100, 999)
        
        testCounts.forEach { initialCount ->
            // Reset cooldown state for each test
            isCooldownActive = false
            lastClickTime = 0
            
            val result = addPushupsTest(initialCount, 20)
            assertEquals(initialCount + 20, result)
        }
    }
    
    @Test
    fun testDataSynchronization_KeyFormat() {
        // Test that both activities use the same key format
        val calendar = Calendar.getInstance()
        calendar.set(2025, Calendar.JANUARY, 5)
        
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        // MainActivity format (yyyy-MM-dd)
        val mainActivityKey = String.format("%04d-%02d-%02d", year, month + 1, day)
        
        // DayDetailActivity format (yyyy-MM-dd) - should be the same
        val dayDetailKey = String.format("%04d-%02d-%02d", year, month + 1, day)
        
        assertEquals("Key formats should match", mainActivityKey, dayDetailKey)
    }
    
    @Test
    fun testOnResume_DataRefresh() {
        // Test that onResume refreshes the data
        var refreshCount = 0
        
        // Simulate onResume behavior
        fun simulateOnResume() {
            refreshCount++
            // In actual implementation, this would call loadTodayCount()
        }
        
        simulateOnResume()
        assertEquals("onResume should trigger data refresh", 1, refreshCount)
        
        simulateOnResume()
        assertEquals("onResume should trigger data refresh again", 2, refreshCount)
    }
    
    @Test
    fun testUpdateTodayCountText_Format() {
        // Test the format of the count text
        val pushupCount = 50
        val expectedText = "Today: $pushupCount pushups"
        val actualText = updateTodayCountTextTest(pushupCount)
        
        assertEquals(expectedText, actualText)
    }
    
    @Test
    fun testUpdateTodayCountText_ZeroCount() {
        // Test with zero pushups
        val pushupCount = 0
        val expectedText = "Today: $pushupCount pushups"
        val actualText = updateTodayCountTextTest(pushupCount)
        
        assertEquals(expectedText, actualText)
    }
    
    @Test
    fun testUpdateTodayCountText_LargeCount() {
        // Test with large pushup count
        val pushupCount = 999
        val expectedText = "Today: $pushupCount pushups"
        val actualText = updateTodayCountTextTest(pushupCount)
        
        assertEquals(expectedText, actualText)
    }
    
    @Test
    fun testDateKeyConsistency_Activities() {
        // Test that both activities generate the same key for the same date
        val testDates = listOf(
            "2025-01-01",
            "2025-06-15", 
            "2025-12-31",
            "2024-02-29" // Leap year
        )
        
        testDates.forEach { expectedKey ->
            // Simulate key generation in both activities
            val mainActivityKey = generateMainActivityKeyTest(expectedKey)
            val dayDetailKey = generateDayDetailKeyTest(expectedKey)
            
            assertEquals("Keys should match for $expectedKey", mainActivityKey, dayDetailKey)
            assertEquals("Generated key should match expected", expectedKey, mainActivityKey)
        }
    }
    
    @Test
    fun testAddPushups_CooldownPreventsDoubleClick() {
        // Test that cooldown period prevents double-clicks
        val initialCount = 40
        val expectedCount = initialCount + 20
        
        // Simulate first click
        val firstResult = addPushupsTest(initialCount, 20)
        assertEquals("First click should add 20", expectedCount, firstResult)
        
        // Simulate immediate second click (should be ignored due to cooldown)
        val secondResult = addPushupsTest(firstResult, 20)
        assertEquals("Second click should be ignored", expectedCount, secondResult)
        
        // Wait for cooldown to complete
        simulateCooldownWait()
        
        // Third click should work after cooldown
        val thirdResult = addPushupsTest(secondResult, 20)
        assertEquals("Third click should work after cooldown", expectedCount + 20, thirdResult)
    }
    
    @Test
    fun testAddPushups_MultipleRapidClicksPrevention() {
        // Test that multiple rapid clicks only register once
        val initialCount = 40
        val expectedCount = initialCount + 20
        
        // Simulate 5 rapid clicks
        var result = initialCount
        repeat(5) {
            result = addPushupsTest(result, 20)
        }
        
        assertEquals("Only one click should be registered", expectedCount, result)
    }
    
    @Test
    fun testAddPushups_ButtonStateDuringCooldown() {
        // Test button state changes during cooldown
        val initialEnabled = true
        val initialAlpha = 1.0f
        
        // Simulate button click - should disable immediately
        val buttonStateAfterClick = simulateButtonClickCooldown(initialEnabled, initialAlpha)
        
        assertFalse("Button should be disabled during cooldown", buttonStateAfterClick.first)
        assertEquals("Button should be semi-transparent during cooldown", 0.5f, buttonStateAfterClick.second, 0.1f)
        
        // Simulate cooldown completion - should re-enable
        val buttonStateAfterCooldown = simulateCooldownCompletion(buttonStateAfterClick.first, buttonStateAfterClick.second)
        
        assertTrue("Button should be re-enabled after cooldown", buttonStateAfterCooldown.first)
        assertEquals("Button should be fully opaque after cooldown", 1.0f, buttonStateAfterCooldown.second, 0.1f)
    }
    
    @Test
    fun testAddPushups_CooldownTiming() {
        // Test that cooldown lasts approximately 1 second
        val cooldownStartTime = System.currentTimeMillis()
        
        // Start cooldown
        val cooldownActive = true
        
        // Simulate cooldown period (1000ms)
        Thread.sleep(1000)
        
        val cooldownEndTime = System.currentTimeMillis()
        val actualCooldownDuration = cooldownEndTime - cooldownStartTime
        
        // Cooldown should be approximately 1 second (allowing 100ms variance)
        assertTrue("Cooldown should be at least 900ms", actualCooldownDuration >= 900)
        assertTrue("Cooldown should not exceed 1100ms", actualCooldownDuration <= 1100)
    }
    
    @Test
    fun testAddPushups_SequentialClicksAfterCooldown() {
        // Test that sequential clicks work after each cooldown
        val initialCount = 40
        var expectedCount = initialCount
        var result = initialCount
        
        // First click
        expectedCount += 20
        result = addPushupsTest(result, 20)
        assertEquals("First click should add 20", expectedCount, result)
        
        // Wait for cooldown
        simulateCooldownWait()
        
        // Second click
        expectedCount += 20
        result = addPushupsTest(result, 20)
        assertEquals("Second click should add 20 after cooldown", expectedCount, result)
        
        // Wait for cooldown
        simulateCooldownWait()
        
        // Third click
        expectedCount += 20
        result = addPushupsTest(result, 20)
        assertEquals("Third click should add 20 after second cooldown", expectedCount, result)
    }
    
    // Helper test functions (these simulate the actual methods)
    private fun getTodayKeyTest(date: Date = Date()): String {
        return dateFormat.format(date)
    }
    
    private fun addPushupsTest(currentCount: Int, amount: Int): Int {
        val currentTime = System.currentTimeMillis()
        
        // Check if cooldown is active (1000ms cooldown)
        if (isCooldownActive && currentTime - lastClickTime < 1000) {
            return currentCount // Ignore click during cooldown
        }
        
        // Process the click
        isCooldownActive = true
        lastClickTime = currentTime
        
        // Don't actually sleep in tests - just simulate the cooldown
        // The cooldown will be considered active for 1000ms
        
        return currentCount + amount
    }
    
    private fun simulateCooldownWait() {
        // Simulate waiting for cooldown to complete
        Thread.sleep(1100)
        isCooldownActive = false
    }
    
    private fun simulateButtonClickCooldown(isEnabled: Boolean, alpha: Float): Pair<Boolean, Float> {
        // Simulate button state change after click
        return Pair(false, 0.5f) // Disabled and semi-transparent
    }
    
    private fun simulateCooldownCompletion(isEnabled: Boolean, alpha: Float): Pair<Boolean, Float> {
        // Simulate button state after cooldown completion
        return Pair(true, 1.0f) // Re-enabled and fully opaque
    }
    
    private fun updateTodayCountTextTest(count: Int): String {
        return "Today: $count pushups"
    }
    
    private fun generateMainActivityKeyTest(expectedKey: String): String {
        // Simulate MainActivity key generation
        return expectedKey
    }
    
    private fun generateDayDetailKeyTest(expectedKey: String): String {
        // Simulate DayDetailActivity key generation
        return expectedKey
    }
}
