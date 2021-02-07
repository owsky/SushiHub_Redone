package com.owsky.sushihubredone.data

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.owsky.sushihubredone.data.dao.TableDao
import com.owsky.sushihubredone.data.entities.Table
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TableRepository @Inject constructor(private val tableDao: TableDao, private val prefs: SharedPreferences) {
    val tablesHistory: LiveData<List<Table>> by lazy { tableDao.getAllButCurrent() }

    suspend fun getMenuPrice(tableCode: String): Double {
        return withContext(CoroutineScope(IO).coroutineContext) {
            tableDao.getMenuPrice(tableCode)
        }
    }

    fun checkoutTable(tableCode: String) {
        CoroutineScope(IO).launch { tableDao.getTable(tableCode)?.let { tableDao.delete(it) } }
    }

    fun createTable(tableCode: String?, restaurant: String, menuPrice: Double) {
        CoroutineScope(IO).launch {
            val editor = prefs.edit()
            val date = Calendar.getInstance().time
            // slave
            if (tableCode != null) {
                val table = tableDao.getTable(tableCode)
                if (table != null) {
                    table.menuPrice = menuPrice
                    table.restaurant = restaurant
                    tableDao.update(table)
                } else {
                    tableDao.insert(Table(tableCode, restaurant, date, menuPrice))
                }
                editor.putString("table_code", tableCode)
                // master
            } else {
                val newCode = UUID.randomUUID().toString()
                editor.putBoolean("is_master", true).putString("table_code", newCode)
                tableDao.insert(Table(newCode, restaurant, date, menuPrice))
            }
            editor.putString("rest_name", restaurant).apply()
        }
    }

    suspend fun getCurrentTable(): Table? {
        return tableDao.getTable(prefs.getString("table_code", null)!!)
    }

    fun deleteAllTables() {
        CoroutineScope(IO).launch { tableDao.deleteAllTables() }
    }
}