package com.owsky.sushihubredone.di

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.owsky.sushihubredone.data.dao.OrderDao
import com.owsky.sushihubredone.data.entities.Order
import com.owsky.sushihubredone.data.entities.OrderStatus
import com.owsky.sushihubredone.util.Connectivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Module
@InstallIn(ViewModelComponent::class)
object ConnectivityModule {

    @Provides
    fun provideConnectivity(prefs: SharedPreferences, payloadCallback: PayloadCallback, @ApplicationContext context: Context): Connectivity {
        return Connectivity.getInstance(prefs.contains("is_master"), prefs.getString("table_code", null)!!, payloadCallback, context)
    }

    @Provides
    fun providePayloadCallback(orderDao: OrderDao): PayloadCallback {
        return object : PayloadCallback() {
            override fun onPayloadReceived(p0: String, p1: Payload) {
                CoroutineScope(Dispatchers.Default).launch {
                    p1.asBytes()?.let {
                        val fromSlave = Order.fromByteArray(it)
                        val dish = fromSlave.dish
                        val table = fromSlave.tableCode
                        val desc = fromSlave.desc
                        val status = fromSlave.status
                        val user = fromSlave.user
                        val price = fromSlave.price
                        when (fromSlave.status) {
                            OrderStatus.InsertOrder ->
                                orderDao.insert(
                                    Order(dish, desc, status, table, user, true, price),
                                )
                            OrderStatus.DeliverOrder -> {
                                val order = orderDao.contains(OrderStatus.Confirmed, table, dish, user)
                                order?.let { ord ->
                                    ord.status = OrderStatus.Delivered
                                    orderDao.update(ord)
                                }
                            }
                            OrderStatus.UndoDeliverOrder -> {
                                val order = orderDao.contains(OrderStatus.Delivered, table, dish, user)
                                order?.let { ord ->
                                    ord.status = OrderStatus.Confirmed
                                    orderDao.update(ord)
                                }
                            }
                            OrderStatus.DeleteOrder -> {
                                val order = orderDao.contains(OrderStatus.Confirmed, table, dish, user)
                                order?.let { ord ->
                                    orderDao.delete(ord)
                                }
                            }
                            else -> Log.d(ContentValues.TAG, "onPayloadReceived: wrong operation status")
                        }
                    }
                }
            }

            override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {
                // noop
            }
        }
    }
}