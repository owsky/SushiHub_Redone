package com.owsky.sushihubredone.di

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.owsky.sushihubredone.data.dao.OrderDao
import com.owsky.sushihubredone.data.entities.Order
import com.owsky.sushihubredone.data.entities.OrderStatus
import com.owsky.sushihubredone.util.Connectivity
import com.owsky.sushihubredone.util.ConnectivityMaster
import com.owsky.sushihubredone.util.ConnectivitySlave
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Qualifier

@Module
@InstallIn(ViewModelComponent::class)
object ConnectivityModule {

//    @Provides
//    fun provideConnectivity(prefs: SharedPreferences, payloadCallback: PayloadCallback, @ApplicationContext context: Context): Connectivity {
//        return Connectivity.getInstance(prefs.contains("is_master"), prefs.getString("table_code", null)!!, payloadCallback, context)
//    }

    @Provides
    fun provideConnectivity(@ApplicationContext context: Context, @Master connectionLifecycleCallback: ConnectionLifecycleCallback, endpointDiscoveryCallback: EndpointDiscoveryCallback): Connectivity {
        val prefs = context.getSharedPreferences("SushiHub_Redone", Context.MODE_PRIVATE)
        return if (prefs.contains("is_master")) {
            ConnectivityMaster(context, connectionLifecycleCallback)
        } else {
            ConnectivitySlave(context, endpointDiscoveryCallback)
        }
    }

//    @Provides
//    @Master
//    fun provideConnectivityMaster(@ApplicationContext context: Context, connectionLifecycleCallback: ConnectionLifecycleCallback): ConnectivityMaster {
//        return ConnectivityMaster(context, connectionLifecycleCallback)
//    }
//
//    @Provides
//    @Slave
//    fun provideConnectivitySlave(@ApplicationContext context: Context, endpointDiscoveryCallback: EndpointDiscoveryCallback): ConnectivitySlave {
//        return ConnectivitySlave(context, endpointDiscoveryCallback)
//    }

    @Provides
    @Master
    fun provideConnectionLifecycleCallback(payloadCallback: PayloadCallback, @ApplicationContext context: Context): ConnectionLifecycleCallback {
        return object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endPointId: String, connectionInfo: ConnectionInfo) {
                Nearby.getConnectionsClient(context).acceptConnection(endPointId, payloadCallback)
            }

            override fun onConnectionResult(p0: String, p1: ConnectionResolution) {
                // noop
            }

            override fun onDisconnected(p0: String) {
                // noop
            }
        }
    }

    @Provides
    @Slave
    fun provideConnectionLifecycleCallbackMaster(@ApplicationContext context: Context): ConnectionLifecycleCallback {
        return object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(p0: String, p1: ConnectionInfo) {
                // noop
            }

            override fun onConnectionResult(endPointId: String, connectionResolution: ConnectionResolution) {
                if (connectionResolution.status.statusCode == ConnectionsStatusCodes.STATUS_OK) {
                    ConnectivitySlave.endPointId = endPointId
                    Nearby.getConnectionsClient(context).stopDiscovery()
                }
            }

            override fun onDisconnected(p0: String) {
                ConnectivitySlave.endPointId = null
            }
        }
    }

    @Provides
    fun provideEndpointDiscoveryCallback(@Slave connectionLifecycleCallback: ConnectionLifecycleCallback, @ApplicationContext context: Context): EndpointDiscoveryCallback {
        return object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(endPointId: String, p1: DiscoveredEndpointInfo) {
                Nearby.getConnectionsClient(context).requestConnection("Slave", endPointId, connectionLifecycleCallback)
            }

            override fun onEndpointLost(p0: String) {
                // noop
            }
        }
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

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Master

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Slave