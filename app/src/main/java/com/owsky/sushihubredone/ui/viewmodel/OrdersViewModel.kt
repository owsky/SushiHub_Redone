package com.owsky.sushihubredone.ui.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.ItemTouchHelper
import com.owsky.sushihubredone.data.OrderRepository
import com.owsky.sushihubredone.data.TableRepository
import com.owsky.sushihubredone.data.entities.Order
import com.owsky.sushihubredone.di.Impl1
import com.owsky.sushihubredone.ui.view.ListOrders
import com.owsky.sushihubredone.ui.view.OrdersAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    @Impl1 private val orderRepository: OrderRepository, private val tableRepository: TableRepository, application: Application
) : AndroidViewModel(application) {

    fun getOrders(type: ListOrders.ListOrdersType): LiveData<List<Order>> {
        return when (type) {
            ListOrders.ListOrdersType.Pending -> orderRepository.pendingOrders!!
            ListOrders.ListOrdersType.Confirmed -> orderRepository.confirmedOrders!!
            ListOrders.ListOrdersType.Delivered -> orderRepository.deliveredOrders!!
            ListOrders.ListOrdersType.Synchronized -> orderRepository.synchronizedOrders!!
        }
    }

    fun getOrdersByTable(tableCode: String): LiveData<List<Order>> {
        return orderRepository.getOrderHistory(tableCode)
    }

    fun insertOrder(order: Order, quantity: Int) {
        orderRepository.insertOrder(order, quantity)
    }

    fun undoInsert() {
        orderRepository.undoLastInsert()
    }

    fun getMenuPrice(): Double = runBlocking {
        withContext(Dispatchers.Default) {
            tableRepository.getMenuPrice()
        }
    }

    fun getExtraPrice(): Double = runBlocking {
        withContext(Dispatchers.Default) {
            orderRepository.getExtraPrice()
        }
    }

    fun checkout(viewModelStoreOwner: ViewModelStoreOwner, tableCode: String) {
        runBlocking {
            orderRepository.checkOut()
            tableRepository.checkoutTable(tableCode)
            viewModelStoreOwner.viewModelStore.clear()
        }
    }

    fun getRecyclerCallback(context: Context, adapter: OrdersAdapter, listOrdersType: ListOrders.ListOrdersType): ItemTouchHelper.SimpleCallback = runBlocking {
        withContext(Dispatchers.Default) {
            orderRepository.getRecyclerCallback(context, adapter, listOrdersType)
        }
    }
}