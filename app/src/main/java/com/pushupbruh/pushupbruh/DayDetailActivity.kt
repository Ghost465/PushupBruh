package com.pushupbruh.pushupbruh

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Button
import android.widget.EditText
import android.content.Intent
import android.app.AlertDialog
import android.view.LayoutInflater
import android.app.ActivityOptions
import android.util.Pair

class DayDetailActivity : AppCompatActivity() {
    private lateinit var pushupCountDisplay: TextView
    private lateinit var prevDayButton: TextView
    private lateinit var nextDayButton: TextView
    private lateinit var dateTitle: TextView
    private lateinit var editButton: TextView
    private lateinit var dataManager: DataManager
    
    private var day = 0
    private var month = 0
    private var year = 0
    private var currentPushupCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_detail)
        
        // Initialize data manager
        dataManager = DataManager(this)
        
        // Get date from intent
        day = intent.getIntExtra("day", 0)
        month = intent.getIntExtra("month", 0)
        year = intent.getIntExtra("year", 0)
        
        initViews()
        loadData()
        setupClickListeners()
    }
    
    private fun initViews() {
        pushupCountDisplay = findViewById(R.id.pushupCountDisplay)
        prevDayButton = findViewById(R.id.prevDayButton)
        nextDayButton = findViewById(R.id.nextDayButton)
        dateTitle = findViewById(R.id.dateTitle)
        editButton = findViewById(R.id.editButton)
        
        // Set date title
        val monthNames = arrayOf("January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December")
        dateTitle.text = "${monthNames[month]} $day, $year"
        title = "${monthNames[month]} $day, $year"
    }
    
    private fun loadData() {
        val dateKey = String.format("%04d-%02d-%02d", year, month + 1, day)
        currentPushupCount = dataManager.getPushupCount(dateKey)
        
        pushupCountDisplay.text = currentPushupCount.toString()
    }
    
    private fun setupClickListeners() {
        editButton.setOnClickListener {
            showEditDialog()
        }
        
        prevDayButton.setOnClickListener {
            navigateToPreviousDay()
        }
        
        nextDayButton.setOnClickListener {
            navigateToNextDay()
        }
        
        // Update button states based on current date
        updateButtonStates()
    }
    
    private fun updateButtonStates() {
        // Check if current date is today or in the future
        val today = java.util.Calendar.getInstance()
        val currentCalendar = java.util.Calendar.getInstance()
        currentCalendar.set(year, month, day)
        
        // Clear time components for accurate date comparison
        today.set(java.util.Calendar.HOUR_OF_DAY, 0)
        today.set(java.util.Calendar.MINUTE, 0)
        today.set(java.util.Calendar.SECOND, 0)
        today.set(java.util.Calendar.MILLISECOND, 0)
        currentCalendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        currentCalendar.set(java.util.Calendar.MINUTE, 0)
        currentCalendar.set(java.util.Calendar.SECOND, 0)
        currentCalendar.set(java.util.Calendar.MILLISECOND, 0)
        
        // Disable next button if current date is today or in the future
        if (currentCalendar.after(today) || currentCalendar.equals(today)) {
            nextDayButton.alpha = 0.5f
            nextDayButton.isEnabled = false
        } else {
            nextDayButton.alpha = 1.0f
            nextDayButton.isEnabled = true
        }
        
        // Disable edit button if current date is in the future
        if (currentCalendar.after(today)) {
            editButton.alpha = 0.5f
            editButton.isEnabled = false
        } else {
            editButton.alpha = 1.0f
            editButton.isEnabled = true
        }
    }
    
    private fun showEditDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_pushup, null)
        val editDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        
        val pushupEdit = dialogView.findViewById<EditText>(R.id.pushupEditDialog)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
        val saveButton = dialogView.findViewById<Button>(R.id.saveButton)
        
        // Set current value
        pushupEdit.setText(currentPushupCount.toString())
        
        cancelButton.setOnClickListener {
            editDialog.dismiss()
        }
        
        saveButton.setOnClickListener {
            val newCount = pushupEdit.text.toString().toIntOrNull()
            
            if (newCount != null && newCount >= 0) {
                currentPushupCount = newCount
                
                // Save using DataManager
                val dateKey = String.format("%04d-%02d-%02d", year, month + 1, day)
                dataManager.savePushupCount(dateKey, currentPushupCount)
                
                // Update display
                pushupCountDisplay.text = currentPushupCount.toString()
                
                editDialog.dismiss()
            } else {
                pushupEdit.error = "Please enter a valid number"
            }
        }
        
        editDialog.show()
    }
    
    private fun navigateToPreviousDay() {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(year, month, day)
        calendar.add(java.util.Calendar.DAY_OF_MONTH, -1)
        
        val newDay = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        val newMonth = calendar.get(java.util.Calendar.MONTH)
        val newYear = calendar.get(java.util.Calendar.YEAR)
        
        // Navigate to previous day with normal transition
        val intent = Intent(this, DayDetailActivity::class.java)
        intent.putExtra("day", newDay)
        intent.putExtra("month", newMonth)
        intent.putExtra("year", newYear)
        
        val options = ActivityOptions.makeCustomAnimation(
            this, 
            R.anim.slide_in_left, 
            R.anim.slide_out_right
        )
        startActivity(intent, options.toBundle())
        finish()
    }
    
    private fun navigateToNextDay() {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(year, month, day)
        calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
        
        val newDay = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        val newMonth = calendar.get(java.util.Calendar.MONTH)
        val newYear = calendar.get(java.util.Calendar.YEAR)
        
        // Check if the next day is in the future
        val today = java.util.Calendar.getInstance()
        val nextDayCalendar = java.util.Calendar.getInstance()
        nextDayCalendar.set(newYear, newMonth, newDay)
        
        // Clear time components for accurate date comparison
        today.set(java.util.Calendar.HOUR_OF_DAY, 0)
        today.set(java.util.Calendar.MINUTE, 0)
        today.set(java.util.Calendar.SECOND, 0)
        today.set(java.util.Calendar.MILLISECOND, 0)
        nextDayCalendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        nextDayCalendar.set(java.util.Calendar.MINUTE, 0)
        nextDayCalendar.set(java.util.Calendar.SECOND, 0)
        nextDayCalendar.set(java.util.Calendar.MILLISECOND, 0)
        
        // Prevent navigation to future dates
        if (nextDayCalendar.after(today)) {
            // Show feedback that future dates are not accessible
            nextDayButton.alpha = 0.5f
            nextDayButton.isEnabled = false
            
            // Re-enable after a short delay
            nextDayButton.postDelayed({
                nextDayButton.alpha = 1.0f
                nextDayButton.isEnabled = true
            }, 500)
            return
        }
        
        // Navigate to next day with reverse transition
        val intent = Intent(this, DayDetailActivity::class.java)
        intent.putExtra("day", newDay)
        intent.putExtra("month", newMonth)
        intent.putExtra("year", newYear)
        
        val options = ActivityOptions.makeCustomAnimation(
            this, 
            R.anim.slide_in_right, 
            R.anim.slide_out_left
        )
        startActivity(intent, options.toBundle())
        finish()
    }
}
