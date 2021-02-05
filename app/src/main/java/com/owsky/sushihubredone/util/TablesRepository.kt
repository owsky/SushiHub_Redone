package com.owsky.sushihubredone.util

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.owsky.sushihubredone.model.AppDatabase
import com.owsky.sushihubredone.model.entities.Table
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class TablesRepository(application: Application) {
    private val tableDao = AppDatabase.getInstance(application).tableDao()
    private val prefs = application.getSharedPreferences("SushiHub_Redone", Context.MODE_PRIVATE)
    val tablesHistory: LiveData<List<Table>> by lazy { tableDao.getAllButCurrent() }

    suspend fun getMenuPrice(tableCode: String): Double {
        return withContext(CoroutineScope(IO).coroutineContext) {
            tableDao.getMenuPrice(tableCode)
        }
    }

    fun checkoutTable(tableCode: String) {
        CoroutineScope(IO).launch { tableDao.getTable(tableCode)?.let { tableDao.delete(it) } }
    }

    fun createTable(restaurant: String, tableCode: String?, menuPrice: Double) {
        CoroutineScope(IO).launch {
            val date = Calendar.getInstance().time
            if (tableCode != null) {
                val table = tableDao.getTable(tableCode)
                if (table != null) {
                    table.menuPrice = menuPrice
                    table.restaurant = restaurant
                } else {
                    tableDao.insert(Table(tableCode, restaurant, date, menuPrice))
                }
            } else {
                val newCode = UUID.randomUUID().toString()
                prefs.edit().putBoolean("is_master", true).apply()
                val table = Table(newCode, restaurant, date, menuPrice)
                tableDao.insert(table)
            }
            prefs.edit().putString("table_code", tableCode).putString("rest_name", restaurant).apply()
        }
    }

    suspend fun getTableAsync(): Table? {
        return withContext(CoroutineScope(IO).coroutineContext) { tableDao.getTable(prefs.getString("table_code", null)!!) }
    }

    fun deleteAllTables() {
        CoroutineScope(IO).launch { tableDao.deleteAllTables() }
    }
}