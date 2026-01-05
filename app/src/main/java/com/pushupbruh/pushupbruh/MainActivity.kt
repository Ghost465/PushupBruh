package com.pushupbruh.pushupbruh

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.cardview.widget.CardView
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.*
import android.os.Vibrator
import android.os.VibrationEffect
import android.content.Context
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var todayCountText: TextView
    private var todayPushupCount = 0
    private lateinit var dataManager: DataManager
    private lateinit var addPushupsCard: CardView
    private var isButtonEnabled = true
    private var vibrator: Vibrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize data manager
        dataManager = DataManager(this)
        
        // Try to restore from backup if needed
        dataManager.restoreFromFile()
        
        val calendarCard = findViewById<CardView>(R.id.calendarCard)
        addPushupsCard = findViewById<CardView>(R.id.addPushupsCard)
        todayCountText = findViewById(R.id.todayCountText)
        
        // Initialize vibrator
        vibrator = try {
            getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        } catch (e: Exception) {
            null
        }
        
        // Load today's pushup count
        
        // Set up click listeners
        calendarCard.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }
        
        addPushupsCard.setOnClickListener {
            addPushups()
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh data when returning from other activities
        loadTodayCount()
        // Force UI update
        updateTodayCountText()
    }
    
    private fun loadTodayCount() {
        // Load today's pushup count using DataManager
        todayPushupCount = dataManager.getPushupCount(getTodayKey())
        updateTodayCountText()
    }
    
    private fun addPushups() {
        if (!isButtonEnabled) return
        
        // Disable button immediately
        isButtonEnabled = false
        addPushupsCard.isEnabled = false
        addPushupsCard.alpha = 0.5f
        
        // Vibrate on click
        try {
            vibrator?.let { vib ->
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    vib.vibrate(100)
                } else {
                    @Suppress("DEPRECATION")
                    vib.vibrate(100)
                }
            }
        } catch (e: Exception) {
            // Vibration failed, but continue without it
        }
        
        todayPushupCount += 20
        updateTodayCountText()
        
        // Save using DataManager (includes automatic backup)
        dataManager.savePushupCount(getTodayKey(), todayPushupCount)
        
        Toast.makeText(this, "Added 20 pushups!", Toast.LENGTH_SHORT).show()
        
        // Re-enable button after 1 second
        addPushupsCard.postDelayed(Runnable {
            isButtonEnabled = true
            addPushupsCard.isEnabled = true
            addPushupsCard.alpha = 1.0f
        }, 1000)
    }
    
    private fun updateTodayCountText() {
        todayCountText.text = "Today: $todayPushupCount pushups"
    }
    
    private fun getTodayKey(): String {
        val date = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(date)
    }
}
