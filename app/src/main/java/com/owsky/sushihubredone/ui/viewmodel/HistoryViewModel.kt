package com.owsky.sushihubredone.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.owsky.sushihubredone.data.OrderRepository
import com.owsky.sushihubredone.data.TableRepository
import com.owsky.sushihubredone.data.entities.Order
import com.owsky.sushihubredone.data.entities.Table
import com.owsky.sushihubredone.di.RepoSansConnect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    application: Application,
    private val tableRepository: TableRepository,
    @RepoSansConnect private val orderRepository: OrderRepository
) : AndroidViewModel(application) {
    val tablesHistory: LiveData<List<Table>> by lazy { tableRepository.getAllButCurrent() }

    fun deleteAllTables() {
        tableRepository.deleteAllTables()
    }

    fun getOrders(tableCode: String): LiveData<List<Order>> {
        return orderRepository.getOrderHistory(tableCode)
    }

    fun deleteTable(table: Table) {
        tableRepository.deleteTable(table)
    }
}