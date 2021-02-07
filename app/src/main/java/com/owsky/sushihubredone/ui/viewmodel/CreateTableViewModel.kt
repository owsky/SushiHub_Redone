package com.owsky.sushihubredone.ui.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import com.owsky.sushihubredone.data.TableRepository
import com.owsky.sushihubredone.data.entities.Table
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreateTableViewModel @Inject constructor(private val tableRepository: TableRepository, private val prefs: SharedPreferences, application: Application) :
    AndroidViewModel(application) {

    fun createTable(tableCode: String?, tableName: String, menuPrice: Double) {
        if (tableCode != null) {
            tableRepository.createTable(tableCode, tableName, menuPrice)
        } else {
            tableRepository.createTable(null, tableName, menuPrice)
        }
    }

    suspend fun getTableInfoAsync(): Table? {
        return tableRepository.getTableAsync()
    }
}