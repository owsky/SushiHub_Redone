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

class TableRepository @Inject constructor(private val tableDao: TableDao, private val prefs: SharedPreferences) {

    suspend fun getMenuPrice() = tableDao.getMenuPrice(prefs.getString("table_code", null)!!)

    private suspend fun getTable(tableCode: String) = tableDao.getTable(tableCode)

    fun getAllButCurrent(): LiveData<List<Table>> {
        return tableDao.getAllButCurrent()
    }

    fun checkoutTable(tableCode: String) {
        CoroutineScope(IO).launch {
            getTable(tableCode)?.let {
                it.isCheckedOut = true
                tableDao.update(it)
            }
        }
    }

    suspend fun createTable(tableCode: String?, restaurant: String, menuPrice: Double) {
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

    suspend fun getCurrentTable(): Table? {
        return tableDao.getTable(prefs.getString("table_code", null)!!)
    }

    fun deleteTable(table: Table) {
        CoroutineScope(IO).launch { tableDao.delete(table) }
    }

    fun deleteAllTables() {
        CoroutineScope(IO).launch { tableDao.deleteAllTables() }
    }
}