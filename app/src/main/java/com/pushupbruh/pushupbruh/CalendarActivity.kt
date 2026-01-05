package com.pushupbruh.pushupbruh

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.content.ContextCompat

class CalendarActivity : AppCompatActivity() {
    private lateinit var monthYearText: TextView
    private lateinit var calendarGrid: GridView
    private lateinit var histogramView: HistogramView
    private val calendar = Calendar.getInstance()
    private var currentMonth = calendar.get(Calendar.MONTH)
    private var currentYear = calendar.get(Calendar.YEAR)
    private lateinit var dataManager: DataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        
        // Initialize data manager
        dataManager = DataManager(this)
        
        monthYearText = findViewById(R.id.monthYearText)
        calendarGrid = findViewById(R.id.calendarGrid)
        histogramView = findViewById(R.id.histogramView)
        
        setupCalendar()
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh calendar data when returning from other activities
        setupCalendar()
    }
    
    private fun setupCalendar() {
        // Set month/year text
        val monthNames = arrayOf("January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December")
        monthYearText.text = "${monthNames[currentMonth]} $currentYear"
        
        // Setup histogram
        histogramView.setMonthlyData(currentYear, currentMonth, dataManager)
        histogramView.updateThemeColors()
        
        // Generate calendar days
        val days = generateCalendarDays()
        val adapter = CalendarAdapter(this, days, currentMonth, currentYear, dataManager)
        calendarGrid.adapter = adapter
        
        // Set click listener for calendar cells
        adapter.setOnItemClickListener { day ->
            if (day > 0) {
                openDayDetail(day, currentMonth, currentYear)
            }
        }
    }
    
    private fun generateCalendarDays(): List<Int> {
        val days = mutableListOf<Int>()
        
        // Get first day of month and number of days
        calendar.set(currentYear, currentMonth, 1)
        val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        // Add empty cells for days before month starts
        for (i in 0 until firstDayOfMonth) {
            days.add(0)
        }
        
        // Add days of month
        for (day in 1..daysInMonth) {
            days.add(day)
        }
        
        return days
    }
    
    private fun openDayDetail(day: Int, month: Int, year: Int) {
        val intent = Intent(this, DayDetailActivity::class.java)
        intent.putExtra("day", day)
        intent.putExtra("month", month)
        intent.putExtra("year", year)
        startActivity(intent)
    }
}

class CalendarAdapter(
    private val context: Context,
    private val days: List<Int>,
    private val currentMonth: Int,
    private val currentYear: Int,
    private val dataManager: DataManager
) : ArrayAdapter<Int>(context, android.R.layout.simple_list_item_1, days) {
    
    private var onItemClickListener: ((Int) -> Unit)? = null
    
    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }
    
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.calendar_cell, parent, false)
        
        val dayText = view.findViewById<TextView>(R.id.dayText)
        val pushupText = view.findViewById<TextView>(R.id.pushupText)
        val day = days[position]
        
        if (day == 0) {
            dayText.text = ""
            pushupText.text = ""
            view.setBackgroundColor(0x00000000) // Transparent
        } else {
            dayText.text = day.toString()
            
            // Get pushup count for this day
            val dateKey = String.format("%04d-%02d-%02d", 
                currentYear,
                currentMonth + 1,
                day)
            val pushupCount = dataManager.getPushupCount(dateKey)
            
            // Check if this is a future date
            val today = Calendar.getInstance()
            val cellDate = Calendar.getInstance()
            cellDate.set(currentYear, currentMonth, day)
            val isFuture = cellDate.after(today)
            
            // Always show pushup count for past and present dates, hide for future
            if (isFuture) {
                pushupText.text = "" // Hide pushup count for future dates
            } else {
                pushupText.text = if (pushupCount > 0) "$pushupCount" else "0"
            }
            
            // Style based on pushup count and date validity
            if (isFuture) {
                // Future dates - no pushup data, just basic styling
                dayText.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                view.setBackgroundResource(R.drawable.calendar_cell_background)
            } else if (pushupCount > 0) {
                // Past/present dates with pushups
                dayText.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                pushupText.setTextColor(ContextCompat.getColor(context, R.color.accent_orange))
                view.setBackgroundResource(R.drawable.calendar_cell_background)
            } else {
                // Past/present dates without pushups
                dayText.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                pushupText.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
                view.setBackgroundResource(R.drawable.calendar_cell_background)
            }
            
            // Make future dates unclickable and visually distinct
            if (isFuture) {
                view.isClickable = false
                view.isEnabled = false
                dayText.alpha = 0.5f // Make future dates semi-transparent
                pushupText.alpha = 0.5f
            } else {
                view.isClickable = true
                view.isEnabled = true
                dayText.alpha = 1.0f
                pushupText.alpha = 1.0f
            }
            
            view.setOnClickListener {
                if (!isFuture) {
                    onItemClickListener?.invoke(day)
                }
            }
        }
        
        return view
    }
}
