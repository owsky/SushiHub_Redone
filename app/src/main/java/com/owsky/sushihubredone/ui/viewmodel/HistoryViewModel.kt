package com.owsky.sushihubredone.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.owsky.sushihubredone.data.OrderRepository
import com.owsky.sushihubredone.data.TableRepository
import com.owsky.sushihubredone.data.entities.Order
import com.owsky.sushihubredone.data.entities.Table
import com.owsky.sushihubredone.di.Impl2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    application: Application,
    private val tableRepository: TableRepository,
    @Impl2 private val orderRepository: OrderRepository
) : AndroidViewModel(application) {
    val tablesHistory: LiveData<List<Table>> by lazy { tableRepository.getAllButCurrent() }

    fun deleteAllTables() {
        tableRepository.deleteAllTables()
    }

    fun getOrders(tableCode: String): LiveData<List<Order>> = runBlocking { orderRepository.getOrderHistory(tableCode) }

    fun deleteTable(table: Table) {
        tableRepository.deleteTable(table)
    }
}