package com.owsky.sushihubredone.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.owsky.sushihubredone.data.TableRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class CreateTableViewModel @Inject constructor(private val tableRepository: TableRepository, application: Application) : AndroidViewModel(application) {
    // these coroutines are blocking because I need to assure that the data is written to the database before it is requested by the QR generation page
    // and the main thread should wait for the data to be retrieved since it's needed to generate the QR
    fun createTable(tableCode: String?, tableName: String, menuPrice: Double) {
        runBlocking {
            if (tableCode != null) {
                tableRepository.createTable(tableCode, tableName, menuPrice)
            } else {
                tableRepository.createTable(null, tableName, menuPrice)
            }
        }
    }

    fun getCurrentTable() = runBlocking {
        tableRepository.getCurrentTable()
    }
}