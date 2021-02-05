package com.owsky.sushihubredone.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.owsky.sushihubredone.R
import com.owsky.sushihubredone.model.entities.Order
import com.owsky.sushihubredone.util.OrdersRepository
import com.owsky.sushihubredone.view.ListOrders
import com.owsky.sushihubredone.view.OrdersAdapter
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class OrdersViewModel(application: Application) : AndroidViewModel(application) {
	private val repository = OrdersRepository(application)

	fun getOrders(type: ListOrders.ListOrdersType): LiveData<List<Order>> {
		return when (type) {
			ListOrders.ListOrdersType.Pending -> repository.pendingOrders
			ListOrders.ListOrdersType.Confirmed -> repository.confirmedOrders
			ListOrders.ListOrdersType.Delivered -> repository.deliveredOrders
			ListOrders.ListOrdersType.Synchronized -> repository.synchronizedOrders
		}
	}

	fun insertOrder(order: Order, quantity: Int) {
		repository.insertOrder(order, quantity)
	}

	fun undoInsert() {
		repository.undoLastInsert()
	}

	suspend fun getRecyclerCallback(
		context: Context,
		adapter: OrdersAdapter,
		listOrdersType: ListOrders.ListOrdersType,
	): ItemTouchHelper.SimpleCallback {
		return repository.getRecyclerCallbackAsync(context, adapter, listOrdersType)
	}
}