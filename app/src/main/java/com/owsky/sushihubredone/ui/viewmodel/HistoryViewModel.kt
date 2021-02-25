package com.owsky.sushihubredone.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.owsky.sushihubredone.data.OrderRepository
import com.owsky.sushihubredone.data.TableRepository
import com.owsky.sushihubredone.data.entities.Order
import com.owsky.sushihubredone.data.entities.Table
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val tableRepository: TableRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {
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