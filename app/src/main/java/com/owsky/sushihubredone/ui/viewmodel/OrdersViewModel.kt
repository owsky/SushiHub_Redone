package com.owsky.sushihubredone.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.ItemTouchHelper
import com.owsky.sushihubredone.data.OrderRepository
import com.owsky.sushihubredone.data.TableRepository
import com.owsky.sushihubredone.data.entities.Order
import com.owsky.sushihubredone.ui.view.ListOrders
import com.owsky.sushihubredone.ui.view.OrdersAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val tableRepository: TableRepository,
    application: Application
) : AndroidViewModel(application) {

    fun getOrders(type: ListOrders.ListOrdersType): LiveData<List<Order>> {
        return when (type) {
            ListOrders.ListOrdersType.Pending -> orderRepository.pendingOrders
            ListOrders.ListOrdersType.Confirmed -> orderRepository.confirmedOrders
            ListOrders.ListOrdersType.Delivered -> orderRepository.deliveredOrders
            ListOrders.ListOrdersType.Synchronized -> orderRepository.synchronizedOrders
        }
    }

    fun insertOrder(order: Order, quantity: Int) {
        orderRepository.insertOrder(order, quantity)
    }

    fun undoInsert() {
        orderRepository.undoLastInsert()
    }

    suspend fun getMenuPrice(tableCode: String): Double {
        return tableRepository.getMenuPrice(tableCode)
    }

    suspend fun getExtraPrice(): Double {
        return orderRepository.getExtraPrice()
    }

    fun checkout(viewModelStoreOwner: ViewModelStoreOwner, tableCode: String) { // TODO fix repo
        orderRepository.checkOut()
        tableRepository.checkoutTable(tableCode)
        viewModelStoreOwner.viewModelStore.clear()
    }

    suspend fun getRecyclerCallback(context: Context, adapter: OrdersAdapter, listOrdersType: ListOrders.ListOrdersType): ItemTouchHelper.SimpleCallback {
        return orderRepository.getRecyclerCallbackAsync(context, adapter, listOrdersType)
    }
}