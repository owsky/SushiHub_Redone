package com.owsky.sushihubredone.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import com.owsky.sushihubredone.data.OrderRepository
import com.owsky.sushihubredone.data.TableRepository
import com.owsky.sushihubredone.data.entities.Order
import com.owsky.sushihubredone.data.entities.OrderStatus
import com.owsky.sushihubredone.ui.view.ListOrders
import com.owsky.sushihubredone.ui.view.OrdersAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository, private val tableRepository: TableRepository
) : ViewModel() {
    private val pendingOrders: LiveData<List<Order>> by lazy {
        orderRepository.getOrdersLiveData(OrderStatus.Pending).asLiveData()
    }
    private val confirmedOrders: LiveData<List<Order>> by lazy {
        orderRepository.getOrdersLiveData(OrderStatus.Confirmed).asLiveData()
    }
    private val deliveredOrders: LiveData<List<Order>> by lazy {
        orderRepository.getOrdersLiveData(OrderStatus.Delivered).asLiveData()
    }
    private val synchronizedOrders: LiveData<List<Order>> by lazy {
        orderRepository.getAllSynchronized().asLiveData()
    }

    fun getOrders(type: ListOrders.ListOrdersType): LiveData<List<Order>> {
        return when (type) {
            ListOrders.ListOrdersType.Pending -> pendingOrders
            ListOrders.ListOrdersType.Confirmed -> confirmedOrders
            ListOrders.ListOrdersType.Delivered -> deliveredOrders
            ListOrders.ListOrdersType.Synchronized -> synchronizedOrders
        }
    }

    fun insertOrder(order: Order, quantity: Int) {
        orderRepository.insertOrder(order, quantity)
    }

    fun undoInsert() {
        orderRepository.undoLastInsert()
    }

    // the coroutines need to be blocking since the data is required to create the view
    fun getMenuPrice(): Double = runBlocking {
        tableRepository.getMenuPrice()
    }

    fun getExtraPrice(): Double = runBlocking {
        orderRepository.getExtraPrice()
    }

    fun checkout(viewModelStoreOwner: ViewModelStoreOwner, tableCode: String) {
        orderRepository.checkOut()
        tableRepository.checkoutTable(tableCode)
        viewModelStoreOwner.viewModelStore.clear()
    }

    fun getRecyclerCallback(context: Context, adapter: OrdersAdapter, listOrdersType: ListOrders.ListOrdersType): ItemTouchHelper.SimpleCallback {
        return orderRepository.getRecyclerCallback(context, adapter, listOrdersType)
    }
}