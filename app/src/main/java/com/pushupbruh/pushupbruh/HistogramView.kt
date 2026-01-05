package com.pushupbruh.pushupbruh

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import java.util.*

class HistogramView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val linePaint = Paint().apply {
        color = lineColor
        strokeWidth = 3f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val pointPaint = Paint().apply {
        color = pointColor
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.parseColor("#2C3E50")
        textSize = 24f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val axisPaint = Paint().apply {
        color = Color.parseColor("#95A5A6")
        strokeWidth = 2f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    
    // Theme-aware colors
    private var lineColor = Color.parseColor("#3498DB")
    private var pointColor = Color.parseColor("#FF8C42")
    private var textColor = Color.parseColor("#2C3E50")
    private var axisColor = Color.parseColor("#95A5A6")

    private var monthlyData: List<Pair<Int, Int>> = emptyList()
    private var currentYear = 0
    private var currentMonth = 0
    
    fun updateThemeColors() {
        // Update colors based on current theme
        lineColor = ContextCompat.getColor(context, R.color.accent_orange)
        pointColor = ContextCompat.getColor(context, R.color.accent_orange)
        textColor = ContextCompat.getColor(context, R.color.text_primary)
        axisColor = ContextCompat.getColor(context, R.color.text_secondary)
        
        // Update paint objects
        linePaint.color = lineColor
        pointPaint.color = pointColor
        textPaint.color = textColor
        axisPaint.color = axisColor
    }

    fun setMonthlyData(year: Int, month: Int, dataManager: DataManager) {
        // Store year and month for use in onDraw
        currentYear = year
        currentMonth = month
        
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1)
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        val data = mutableListOf<Pair<Int, Int>>()
        val today = Calendar.getInstance()
        
        // Get data for each day, but exclude future dates
        for (day in 1..daysInMonth) {
            val dayCalendar = Calendar.getInstance()
            dayCalendar.set(year, month, day)
            
            // Clear time components for accurate date comparison
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)
            dayCalendar.set(Calendar.HOUR_OF_DAY, 0)
            dayCalendar.set(Calendar.MINUTE, 0)
            dayCalendar.set(Calendar.SECOND, 0)
            dayCalendar.set(Calendar.MILLISECOND, 0)
            
            // Only include past and present dates in the graph
            if (!dayCalendar.after(today)) {
                val dateKey = String.format("%04d-%02d-%02d", year, month + 1, day)
                val pushupCount = dataManager.getPushupCount(dateKey)
                data.add(Pair(day, pushupCount))
            } else {
                // Add future dates with 0 count to maintain spacing but no visual points
                data.add(Pair(day, 0))
            }
        }
        
        monthlyData = data
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        if (monthlyData.isEmpty()) return
        
        val width = width.toFloat()
        val height = height.toFloat()
        val padding = 40f
        val graphWidth = width - 2 * padding
        val graphHeight = height - 2 * padding
        
        // Find max value for scaling
        val maxValue = monthlyData.maxOfOrNull { it.second } ?: 1
        if (maxValue == 0) return
        
        // Draw axes
        canvas.drawLine(padding, height - padding, width - padding, height - padding, axisPaint)
        canvas.drawLine(padding, padding, padding, height - padding, axisPaint)
        
        // Calculate points for line graph
        val points = mutableListOf<Pair<Float, Float>>()
        val xStep = graphWidth / (monthlyData.size - 1)
        val today = Calendar.getInstance()
        
        monthlyData.forEachIndexed { index, (day, value) ->
            val dayCalendar = Calendar.getInstance()
            dayCalendar.set(currentYear, currentMonth, day)
            
            // Clear time components for accurate date comparison
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)
            dayCalendar.set(Calendar.HOUR_OF_DAY, 0)
            dayCalendar.set(Calendar.MINUTE, 0)
            dayCalendar.set(Calendar.SECOND, 0)
            dayCalendar.set(Calendar.MILLISECOND, 0)
            
            val x = padding + index * xStep
            val y = height - padding - (value.toFloat() / maxValue) * graphHeight
            
            // Only add points for past/present dates (including 0 values)
            // This ensures line continuity through days with no pushups
            // but excludes future dates completely
            if (!dayCalendar.after(today)) {
                points.add(Pair(x, y))
            }
        }
        
        // Draw line connecting past/present points only
        if (points.size > 1) {
            for (i in 0 until points.size - 1) {
                val start = points[i]
                val end = points[i + 1]
                canvas.drawLine(start.first, start.second, end.first, end.second, linePaint)
            }
        }
        
        // Draw points and labels
        val labelInterval = maxOf(1, monthlyData.size / 10) // Show max 10 labels
        monthlyData.forEachIndexed { index, (day, value) ->
            val x = padding + index * xStep
            val y = height - padding - (value.toFloat() / maxValue) * graphHeight
            
            // Only draw points for past/present dates (value > 0)
            if (value > 0) {
                canvas.drawCircle(x, y, 6f, pointPaint)
            }
            
            // Draw value on points with data (only for past/present dates)
            if (value > 0 && index % labelInterval == 0) {
                canvas.drawText(value.toString(), x, y - 15f, textPaint)
            }
            
            // Draw day labels
            if (index % labelInterval == 0) {
                val labelPaint = Paint().apply {
                    color = Color.parseColor("#7F8C8D")
                    textSize = 20f
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.drawText(day.toString(), x, height - padding + 25f, labelPaint)
            }
        }
        
        // Draw title
        val titlePaint = Paint().apply {
            color = Color.parseColor("#2C3E50")
            textSize = 28f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Daily Pushups", width / 2f, 30f, titlePaint)
    }
}
