package com.owsky.sushihubredone.data

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.owsky.sushihubredone.R
import com.owsky.sushihubredone.data.dao.OrderDao
import com.owsky.sushihubredone.data.entities.Order
import com.owsky.sushihubredone.data.entities.OrderStatus
import com.owsky.sushihubredone.ui.view.ListOrders
import com.owsky.sushihubredone.ui.view.OrdersAdapter
import com.owsky.sushihubredone.util.Connectivity
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val application: Application,
    private val orderDao: OrderDao,
    private val prefs: SharedPreferences,
    private val connectivity: Connectivity?
) {
    private val table = prefs.getString("table_code", null)

    fun getOrdersLiveData(orderStatus: OrderStatus): Flow<List<Order>> {
        return orderDao.getAllByStatus(orderStatus, table!!)
    }

    fun getAllSynchronized(): Flow<List<Order>> {
        return orderDao.getAllSynchronized(table!!)
    }

    private var lastInsertedIds = LongArray(99)
    fun insertOrder(order: Order, quantity: Int) {
        CoroutineScope(IO).launch { lastInsertedIds = orderDao.insertAll(*Array(quantity) { order }) }
    }

    private fun updateOrder(order: Order) {
        CoroutineScope(IO).launch { orderDao.update(order) }
    }

    private fun deleteOrder(order: Order) {
        CoroutineScope(IO).launch { orderDao.delete(order) }
    }

    fun undoLastInsert() {
        CoroutineScope(IO).launch { orderDao.deleteAllById(lastInsertedIds) }
    }

    private fun confirmOrder(order: Order) {
        CoroutineScope(IO).launch {
            order.status = OrderStatus.Confirmed
            updateOrder(order)

            if (!prefs.contains("is_master")) {
                connectivity?.sendPayload(Order.toByteArray(order))
            }
        }
    }

    private fun undoConfirmOrder(order: Order) {
        CoroutineScope(IO).launch {
            order.status = OrderStatus.Pending
            updateOrder(order)

            if (!prefs.contains("is_master")) {
                connectivity?.sendPayload(Order.toByteArray(order))
            }
        }
    }

    private fun markAsDelivered(order: Order) {
        CoroutineScope(IO).launch {
            order.status = OrderStatus.Delivered
            updateOrder(order)

            if (!prefs.contains("is_master")) {
                connectivity?.sendPayload(Order.toByteArray(order))
            }
        }
    }

    private fun undoMarkAsDelivered(order: Order) {
        CoroutineScope(IO).launch {
            order.status = OrderStatus.Confirmed
            updateOrder(order)

            if (!prefs.contains("is_master")) {
                connectivity?.sendPayload(Order.toByteArray(order))
            }
        }
    }

    suspend fun getExtraPrice(): Double {
        return orderDao.getExtraPrice(table!!)
    }

    private fun cleanDatabase() {
        CoroutineScope(IO).launch { orderDao.deleteSlaves() }
    }

    fun checkOut() {
        if (prefs.contains("is_master"))
            cleanDatabase()
        prefs.edit().clear().apply()
        Connectivity.disconnect(application)
    }

    fun getOrderHistory(tableCode: String): Flow<List<Order>> {
        return orderDao.getAllByTable(tableCode)
    }

    fun getRecyclerCallback(context: Context, adapter: OrdersAdapter, listOrdersType: ListOrders.ListOrdersType): ItemTouchHelper.SimpleCallback {
        return when (listOrdersType) {
            ListOrders.ListOrdersType.Pending -> makeRecyclerCallback(
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                { integer -> confirmOrder(adapter.getOrderAt(integer)) },
                { integer -> deleteOrder(adapter.getOrderAt(integer)) },
                ContextCompat.getColor(context, R.color.colorPrimary),
                ContextCompat.getColor(context, R.color.red),
                R.drawable.ic_send, R.drawable.ic_delete
            )
            ListOrders.ListOrdersType.Confirmed -> makeRecyclerCallback(
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                { integer -> markAsDelivered(adapter.getOrderAt(integer)) },
                { integer -> undoConfirmOrder(adapter.getOrderAt(integer)) },
                ContextCompat.getColor(context, R.color.colorPrimary),
                ContextCompat.getColor(context, R.color.colorPrimary),
                R.drawable.ic_confirmed, R.drawable.ic_send_rev
            )
            ListOrders.ListOrdersType.Delivered -> makeRecyclerCallback(
                ItemTouchHelper.LEFT,
                null,
                { integer -> undoMarkAsDelivered(adapter.getOrderAt(integer)) },
                0,
                ContextCompat.getColor(context, R.color.colorPrimary),
                0, R.drawable.ic_send_rev
            )
            else -> makeRecyclerCallback(0, null, null, 0, 0, 0, 0)
        }
    }

    private fun makeRecyclerCallback(
        dragDir2: Int,
        consumerRight: Consumer<Int>?,
        consumerLeft: Consumer<Int>?,
        colorRight: Int,
        colorLeft: Int,
        drawableRight: Int,
        drawableLeft: Int
    ): ItemTouchHelper.SimpleCallback {
        return object : ItemTouchHelper.SimpleCallback(0, dragDir2) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.RIGHT)
                    consumerRight?.accept(viewHolder.adapterPosition)
                else if (direction == ItemTouchHelper.LEFT)
                    consumerLeft?.accept(viewHolder.adapterPosition)
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                when {
                    dX < 0 -> {
                        val decorator = RecyclerViewSwipeDecorator(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        decorator.setBackgroundColor(colorLeft)
                        decorator.setSwipeLeftActionIconId(drawableLeft)
                        decorator.decorate()
                    }
                    dX > 0 -> {
                        val decorator = RecyclerViewSwipeDecorator(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        decorator.setBackgroundColor(colorRight)
                        decorator.setSwipeRightActionIconId(drawableRight)
                        decorator.decorate()
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
    }
}
