package com.owsky.sushihubredone.data

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.lifecycle.LiveData
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OrderRepository @Inject constructor(private val orderDao: OrderDao, private val prefs: SharedPreferences, private val connectivity: Connectivity?) {
    private val table = prefs.getString("table_code", null)
    val pendingOrders by lazy {
        if (table != null) {
            orderDao.getAllByStatus(OrderStatus.Pending, table)
        } else null
    }
    val confirmedOrders by lazy {
        if (table != null) {
            orderDao.getAllByStatus(OrderStatus.Confirmed, table)
        } else null
    }
    val deliveredOrders by lazy {
        if (table != null) {
            orderDao.getAllByStatus(OrderStatus.Delivered, table)
        } else null
    }
    val synchronizedOrders by lazy {
        if (table != null) {
            orderDao.getAllSynchronized(table)
        } else null
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
                connectivity?.send(Order.toByteArray(order))
            }
        }
    }

    private fun undoConfirmOrder(order: Order) {
        CoroutineScope(IO).launch {
            order.status = OrderStatus.Pending
            updateOrder(order)

            if (!prefs.contains("is_master")) {
                connectivity?.send(Order.toByteArray(order))
            }
        }
    }

    private fun markAsDelivered(order: Order) {
        CoroutineScope(IO).launch {
            order.status = OrderStatus.Delivered
            updateOrder(order)

            if (!prefs.contains("is_master")) {
                connectivity?.send(Order.toByteArray(order))
            }
        }
    }

    private fun undoMarkAsDelivered(order: Order) {
        CoroutineScope(IO).launch {
            order.status = OrderStatus.Confirmed
            updateOrder(order)

            if (!prefs.contains("is_master")) {
                connectivity?.send(Order.toByteArray(order))
            }
        }
    }

    suspend fun getExtraPrice(): Double {
        return withContext(CoroutineScope(IO).coroutineContext) {
            orderDao.getExtraPrice(table!!)
        }
    }

    private fun cleanDatabase() {
        CoroutineScope(IO).launch { orderDao.deleteSlaves() }
    }

    fun checkOut() {
        if (prefs.contains("is_master"))
            cleanDatabase()
        prefs.edit().clear().apply()
//        this.connectivity?.disconnect()
        Connectivity.disconnect()
    }

    fun getOrderHistory(tableCode: String): LiveData<List<Order>> {
        return orderDao.getAllByTable(tableCode)
    }

    fun getRecyclerCallback(context: Context, adapter: OrdersAdapter, listOrdersType: ListOrders.ListOrdersType): ItemTouchHelper.SimpleCallback {
        return when (listOrdersType) {
            ListOrders.ListOrdersType.Pending -> makeRecyclerCallback(
                context,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                { integer -> confirmOrder(adapter.getOrderAt(integer)) },
                { integer -> deleteOrder(adapter.getOrderAt(integer)) },
                ContextCompat.getColor(context, R.color.colorPrimary),
                ContextCompat.getColor(context, R.color.red),
                R.drawable.ic_send, R.drawable.ic_delete
            )
            ListOrders.ListOrdersType.Confirmed -> makeRecyclerCallback(
                context,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                { integer -> markAsDelivered(adapter.getOrderAt(integer)) },
                { integer -> undoConfirmOrder(adapter.getOrderAt(integer)) },
                ContextCompat.getColor(context, R.color.colorPrimary),
                ContextCompat.getColor(context, R.color.colorPrimary),
                R.drawable.ic_confirmed, R.drawable.ic_send_rev
            )
            ListOrders.ListOrdersType.Delivered -> makeRecyclerCallback(
                context,
                ItemTouchHelper.LEFT,
                null,
                { integer -> undoMarkAsDelivered(adapter.getOrderAt(integer)) },
                0,
                ContextCompat.getColor(context, R.color.colorPrimary),
                0, R.drawable.ic_send_rev
            )
            else -> makeRecyclerCallback(context, 0, null, null, 0, 0, 0, 0)
        }
    }

    private fun makeRecyclerCallback(
        context: Context,
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
                        RecyclerViewSwipeDecorator.Builder(context, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addBackgroundColor(colorLeft)
                            .addSwipeLeftActionIcon(drawableLeft)
                            .create()
                            .decorate()
                    }
                    dX > 0 -> {
                        RecyclerViewSwipeDecorator.Builder(context, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addBackgroundColor(colorRight)
                            .addSwipeRightActionIcon(drawableRight)
                            .create()
                            .decorate()
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
    }
}
