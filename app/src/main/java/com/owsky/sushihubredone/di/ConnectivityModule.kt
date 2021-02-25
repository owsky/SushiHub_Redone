package com.owsky.sushihubredone.di

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.owsky.sushihubredone.data.dao.OrderDao
import com.owsky.sushihubredone.data.entities.Order
import com.owsky.sushihubredone.data.entities.OrderStatus
import com.owsky.sushihubredone.util.Connectivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object ConnectivityModule {

    @Provides
    fun provideConnectivity(
        @ApplicationContext context: Context,
        prefs: SharedPreferences,
        @Master connectionLifecycleCallback: ConnectionLifecycleCallback,
        endpointDiscoveryCallback: EndpointDiscoveryCallback,
    ): Connectivity {
        return if (prefs.contains("is_master")) {
            Connectivity(context as Application, prefs, connectionLifecycleCallback, null)
        } else {
            Connectivity(context as Application, prefs, null, endpointDiscoveryCallback)
        }
    }

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
                    Connectivity.endPointId = endPointId
                    Nearby.getConnectionsClient(context).stopDiscovery()
                }
            }

            override fun onDisconnected(p0: String) {
                Connectivity.endPointId = null
            }
        }
    }

    @Provides
    fun provideEndpointDiscoveryCallback(
        @Slave connectionLifecycleCallback: ConnectionLifecycleCallback,
        @ApplicationContext context: Context
    ): EndpointDiscoveryCallback {
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