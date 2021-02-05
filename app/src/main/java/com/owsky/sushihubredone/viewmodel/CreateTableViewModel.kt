package com.owsky.sushihubredone.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.owsky.sushihubredone.model.entities.Table
import com.owsky.sushihubredone.util.TablesRepository
import java.util.*

class CreateTableViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("SushiHub_Redone", Context.MODE_PRIVATE)
    private val tableRepository = TablesRepository(application)

    fun createTable(tableCode: String?, tableName: String, menuPrice: Double) {
        val editor = prefs.edit()
        if (tableCode != null) {
            editor.putString("table_code", tableCode).apply()
            tableRepository.createTable(tableCode, tableName, menuPrice)
        } else {
            val newCode = UUID.randomUUID().toString()
            editor.putString("table_code", newCode).putBoolean("is_master", true).apply()
            tableRepository.createTable(newCode, tableName, menuPrice)
        }
    }

    suspend fun getTableInfo(): Table? {
        return tableRepository.getTableAsync()
    }
}