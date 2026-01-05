package com.pushupbruh.pushupbruh

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject
import org.json.JSONArray
import java.io.File
import java.io.FileWriter
import java.io.FileReader

class DataManager(private val context: Context) {
    
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences("PushupData", Context.MODE_PRIVATE)
    private val dataFile = File(context.filesDir, "pushup_data.json")
    
    // Current method - SharedPreferences (fast, simple)
    fun savePushupCount(date: String, count: Int) {
        sharedPrefs.edit().putInt(date, count).apply()
        // Also backup to file for extra safety
        backupToFile()
    }
    
    fun getPushupCount(date: String): Int {
        return sharedPrefs.getInt(date, 0)
    }
    
    // Backup method - File storage (more robust)
    private fun backupToFile() {
        try {
            val allData = sharedPrefs.all
            val jsonObject = JSONObject()
            
            allData.forEach { (key, value) ->
                if (value is Int) {
                    jsonObject.put(key, value)
                }
            }
            
            FileWriter(dataFile).use { writer ->
                writer.write(jsonObject.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    // Restore from file backup
    fun restoreFromFile() {
        try {
            if (!dataFile.exists()) return
            
            val content = FileReader(dataFile).readText()
            val jsonObject = JSONObject(content)
            
            val editor = sharedPrefs.edit()
            jsonObject.keys().forEach { key ->
                editor.putInt(key, jsonObject.getInt(key))
            }
            editor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    // Export data for user backup
    fun exportData(): String {
        val allData = sharedPrefs.all
        val jsonArray = JSONArray()
        
        allData.forEach { (date, count) ->
            if (count is Int && count > 0) {
                val item = JSONObject()
                item.put("date", date)
                item.put("pushups", count)
                jsonArray.put(item)
            }
        }
        
        return jsonArray.toString()
    }
    
    // Import data from user backup
    fun importData(jsonData: String) {
        try {
            val jsonArray = JSONArray(jsonData)
            val editor = sharedPrefs.edit()
            
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val date = item.getString("date")
                val pushups = item.getInt("pushups")
                editor.putInt(date, pushups)
            }
            
            editor.apply()
            backupToFile()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
