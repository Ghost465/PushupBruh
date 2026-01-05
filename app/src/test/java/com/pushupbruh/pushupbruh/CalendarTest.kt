package com.pushupbruh.pushupbruh

import org.junit.Test
import org.junit.Assert.*

class CalendarTest {
    
    @Test
    fun testDateKeyGeneration() {
        // Test that date key is generated correctly
        val year = 2025
        val month = 0 // January
        val day = 15
        
        val dateKey = String.format("%04d-%02d-%02d", year, month + 1, day)
        
        assertEquals("2025-01-15", dateKey)
    }
    
    @Test
    fun testDateKeyGeneration_February() {
        // Test February date key
        val year = 2025
        val month = 1 // February
        val day = 28
        
        val dateKey = String.format("%04d-%02d-%02d", year, month + 1, day)
        
        assertEquals("2025-02-28", dateKey)
    }
    
    @Test
    fun testDateKeyGeneration_December() {
        // Test December date key
        val year = 2025
        val month = 11 // December
        val day = 31
        
        val dateKey = String.format("%04d-%02d-%02d", year, month + 1, day)
        
        assertEquals("2025-12-31", dateKey)
    }
    
    @Test
    fun testPushupCountAddition() {
        // Test that pushup count addition works correctly
        var currentCount = 10
        val addition = 20
        
        currentCount += addition
        
        assertEquals(30, currentCount)
    }
    
    @Test
    fun testPushupCountZero() {
        // Test zero pushup count
        val count = 0
        
        assertEquals(0, count)
    }
}
