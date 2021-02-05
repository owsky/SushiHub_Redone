package com.owsky.sushihubredone.util

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Canvas
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.owsky.sushihubredone.R
import com.owsky.sushihubredone.model.AppDatabase
import com.owsky.sushihubredone.model.entities.Order
import com.owsky.sushihubredone.model.entities.OrderStatus
import com.owsky.sushihubredone.view.ListOrders
import com.owsky.sushihubredone.view.OrdersAdapter
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrdersRepository(application: Application) {
    private val orderDao = AppDatabase.getInstance(application).orderDao()
    private val prefs = application.getSharedPreferences("SushiHub_Redone", Context.MODE_PRIVATE)
    private val table = prefs.getString("table_code", null)!!
    val pendingOrders by lazy {
        orderDao.getAllByStatus(OrderStatus.Pending, table)
    }
    val confirmedOrders by lazy {
        orderDao.getAllByStatus(OrderStatus.Confirmed, table)
    }
    val deliveredOrders by lazy {
        orderDao.getAllByStatus(OrderStatus.Delivered, table)
    }
    val synchronizedOrders by lazy {
        orderDao.getAllSynchronized(table)
    }
    private var lastInsertedIds = LongArray(99)

    fun insertOrder(order: Order, quantity: Int) {
        CoroutineScope(IO).launch {
            lastInsertedIds = orderDao.insertAll(*Array(quantity) { order })
        }
    }

    fun updateOrder(order: Order) {
        CoroutineScope(IO).launch { orderDao.update(order) }
    }

    fun deleteOrder(order: Order) {
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
                // Nearby
            }
        }
    }

    private fun undoConfirmOrder(order: Order) {
        CoroutineScope(IO).launch {
            order.status = OrderStatus.Pending
            updateOrder(order)

            if (!prefs.contains("is_master")) {
                //Nearby
            }
        }
    }

    private fun markAsDelivered(order: Order) {
        CoroutineScope(IO).launch {
            order.status = OrderStatus.Delivered
            updateOrder(order)

            if (!prefs.contains("is_master")) {
                // Nearby}
            }
        }
    }

    private fun undoMarkAsDelivered(order: Order) {
        CoroutineScope(IO).launch {
            order.status = OrderStatus.Confirmed
            updateOrder(order)

            if (!prefs.contains("is_master")) {
                // Nearby
            }
        }
    }

    private fun cleanDatabase() {
        CoroutineScope(IO).launch { orderDao.deleteSlaves() }
    }

    fun checkOut() {
        if (prefs.contains("is_master"))
            cleanDatabase()
        prefs.edit().clear().apply()
        // TODO Nearby close connection
    }

    fun getPayloadCallback(): PayloadCallback {
        return object : PayloadCallback() {
            override fun onPayloadReceived(p0: String, p1: Payload) {
                CoroutineScope(Dispatchers.Default).launch {
                    p1.asBytes()?.let {
                        val fromSlave = Order.fromByteArray(it)
                        val dish = fromSlave.dish
                        val desc = fromSlave.desc
                        val status = fromSlave.status
                        val user = fromSlave.user
                        val price = fromSlave.price
                        when (fromSlave.status) {
                            OrderStatus.InsertOrder -> insertOrder(
                                Order(dish, desc, status, table, user, true, price), 1
                            )
                            OrderStatus.DeliverOrder -> {
                                val order =
                                    orderDao.contains(OrderStatus.Confirmed, table, dish, user)
                                order?.let { ord ->
                                    ord.status = OrderStatus.Delivered
                                    updateOrder(ord)
                                }
                            }
                            OrderStatus.UndoDeliverOrder -> {
                                val order =
                                    orderDao.contains(OrderStatus.Delivered, table, dish, user)
                                order?.let { ord ->
                                    ord.status = OrderStatus.Confirmed
                                    updateOrder(ord)
                                }
                            }
                            OrderStatus.DeleteOrder -> {
                                val order =
                                    orderDao.contains(OrderStatus.Confirmed, table, dish, user)
                                order?.let { ord ->
                                    deleteOrder(ord)
                                }
                            }
                            else -> Log.d(TAG, "onPayloadReceived: wrong operation status")
                        }
                    }
                }
            }

            override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {
                // noop
            }
        }
    }

    suspend fun getRecyclerCallbackAsync(
        context: Context,
        adapter: OrdersAdapter,
        listOrdersType: ListOrders.ListOrdersType
    ): ItemTouchHelper.SimpleCallback {
        return withContext(CoroutineScope(IO).coroutineContext) {
            when (listOrdersType) {
                ListOrders.ListOrdersType.Pending -> makeRecyclerCallback(
                    context,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                    { integer -> confirmOrder(adapter.getOrderAt(integer)) },
                    { integer -> deleteOrder(adapter.getOrderAt(integer)) },
                    ContextCompat.getColor(context, R.color.colorPrimary),
                    ContextCompat.getColor(context, R.color.red),
                    R.drawable.ic_send, R.drawable.ic_confirmed
                )
                ListOrders.ListOrdersType.Confirmed -> makeRecyclerCallback(
                    context,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                    { integer -> markAsDelivered(adapter.getOrderAt(integer)) },
                    { integer -> undoConfirmOrder(adapter.getOrderAt(integer)) },
                    ContextCompat.getColor(context, R.color.colorPrimary),
                    ContextCompat.getColor(context, R.color.colorPrimary),
                    R.drawable.ic_send, R.drawable.ic_send_rev
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
        override fun onMove(
            recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if (direction == ItemTouchHelper.RIGHT)
                consumerRight?.accept(viewHolder.adapterPosition)
            else if (direction == ItemTouchHelper.LEFT)
                consumerLeft?.accept(viewHolder.adapterPosition)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            when {
                dX < 0 -> {
                    RecyclerViewSwipeDecorator.Builder(
                        context,
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                        .addBackgroundColor(colorLeft)
                        .addSwipeLeftActionIcon(drawableLeft)
                        .create()
                        .decorate()
                }
                dX > 0 -> {
                    RecyclerViewSwipeDecorator.Builder(
                        context,
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                        .addBackgroundColor(colorRight)
                        .addSwipeRightActionIcon(drawableRight)
                        .create()
                        .decorate()
                }
            }
            super.onChildDraw(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
        }
    }
}
