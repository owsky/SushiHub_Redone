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
import com.owsky.sushihubredone.di.RepoWithConnect
import com.owsky.sushihubredone.ui.view.ListOrders
import com.owsky.sushihubredone.ui.view.OrdersAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    @RepoWithConnect private val orderRepository: OrderRepository, private val tableRepository: TableRepository, application: Application
) : AndroidViewModel(application) {

    fun getOrders(type: ListOrders.ListOrdersType): LiveData<List<Order>> {
        return when (type) {
            ListOrders.ListOrdersType.Pending -> orderRepository.pendingOrders!!
            ListOrders.ListOrdersType.Confirmed -> orderRepository.confirmedOrders!!
            ListOrders.ListOrdersType.Delivered -> orderRepository.deliveredOrders!!
            ListOrders.ListOrdersType.Synchronized -> orderRepository.synchronizedOrders!!
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