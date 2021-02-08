package com.owsky.sushihubredone.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.owsky.sushihubredone.data.TableRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CreateTableViewModel @Inject constructor(private val tableRepository: TableRepository, application: Application) :
    AndroidViewModel(application) {

    fun createTable(tableCode: String?, tableName: String, menuPrice: Double) {
        // this coroutine is practically synchronous to avoid crashes/missing data while rendering the QR code
        runBlocking {
            launch {
                if (tableCode != null) {
                    tableRepository.createTable(tableCode, tableName, menuPrice)
                } else {
                    tableRepository.createTable(null, tableName, menuPrice)
                }
            }.join()
        }
    }

    fun getCurrentTable() = runBlocking {
        withContext(Dispatchers.Default) {
            tableRepository.getCurrentTable()
        }
    }
}