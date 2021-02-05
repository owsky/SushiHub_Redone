package com.owsky.sushihubredone.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.ItemTouchHelper
import com.owsky.sushihubredone.model.entities.Order
import com.owsky.sushihubredone.model.entities.Table
import com.owsky.sushihubredone.util.OrdersRepository
import com.owsky.sushihubredone.util.TablesRepository
import com.owsky.sushihubredone.view.ListOrders
import com.owsky.sushihubredone.view.OrdersAdapter

class OrdersViewModel(application: Application) : AndroidViewModel(application) {
    private val ordersRepository by lazy { OrdersRepository(application) }
    private val tableRepository by lazy { TablesRepository(application) }

    fun getOrders(type: ListOrders.ListOrdersType): LiveData<List<Order>> {
        return when (type) {
			ListOrders.ListOrdersType.Pending -> ordersRepository.pendingOrders
			ListOrders.ListOrdersType.Confirmed -> ordersRepository.confirmedOrders
			ListOrders.ListOrdersType.Delivered -> ordersRepository.deliveredOrders
			ListOrders.ListOrdersType.Synchronized -> ordersRepository.synchronizedOrders
        }
    }

    fun insertOrder(order: Order, quantity: Int) {
        ordersRepository.insertOrder(order, quantity)
    }

    fun undoInsert() {
        ordersRepository.undoLastInsert()
    }

    suspend fun getMenuPrice(tableCode: String) : Double {
        return tableRepository.getMenuPrice(tableCode)
    }

    suspend fun getExtraPrice() : Double {
        return ordersRepository.getExtraPrice()
    }

    fun checkout(viewModelStoreOwner: ViewModelStoreOwner, tableCode: String) { // TODO fix repo
        ordersRepository.checkOut()
        tableRepository.checkoutTable(tableCode)
        viewModelStoreOwner.viewModelStore.clear()
    }

    suspend fun getRecyclerCallback(context: Context, adapter: OrdersAdapter, listOrdersType: ListOrders.ListOrdersType, ): ItemTouchHelper.SimpleCallback {
        return ordersRepository.getRecyclerCallbackAsync(context, adapter, listOrdersType)
    }
}