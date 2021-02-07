package com.owsky.sushihubredone.util

import android.app.Application
import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.concurrent.locks.ReentrantLock

class Connectivity private constructor(
    isClient: Boolean,
    private val SERVICE_ID: String,
    private val callback: PayloadCallback,
    private val application: Application
) {
    private val strategy = Strategy.P2P_STAR
    private val reentrantLock = ReentrantLock()
    private var isConnected: Boolean = false
    private var strendPointId: String? = null

    init {
        if (isClient)
            startDiscovery()
        else
            startAdvertising()
    }

    private fun startAdvertising() {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(strategy).build()
        Nearby.getConnectionsClient(application).startAdvertising("Device A", SERVICE_ID, object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endPointId: String, connectionInfo: ConnectionInfo) {
//                Nearby.getConnectionsClient(application).acceptConnection(endPointId, callback)
            }

            override fun onConnectionResult(endPointId: String, connectionResolution: ConnectionResolution) {
//                if (connectionResolution.status.statusCode == ConnectionsStatusCodes.STATUS_OK) {
//                    strendPointId = endPointId
//                    reentrantLock.withLock {
//                        isConnected = true
//                        reentrantLock.unlock()
//                    }
//                }
            }

            override fun onDisconnected(p0: String) {
//                isConnected = false
//                strendPointId = null
            }
        }, advertisingOptions)
    }

    private fun startDiscovery() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(strategy).build()
        val connectionsClient = Nearby.getConnectionsClient(application)
        connectionsClient.startDiscovery(SERVICE_ID, object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(endpointId: String, discoveredEndpointInfo: DiscoveredEndpointInfo) {
                connectionsClient.requestConnection("Device B", endpointId, object : ConnectionLifecycleCallback() {
                    override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                        connectionsClient.acceptConnection(endpointId, callback)
                        strendPointId = endpointId
                    }

                    override fun onConnectionResult(s: String, connectionResolution: ConnectionResolution) {
                        if (connectionResolution.status.statusCode == ConnectionsStatusCodes.STATUS_OK) {
                            isConnected = true
                            reentrantLock.unlock()
                        }
                    }

                    override fun onDisconnected(s: String) {
                        isConnected = false
                    }
                })
            }

            override fun onEndpointLost(p0: String) {
                isConnected = false
            }
        }, discoveryOptions)
    }


    private fun sendPayload(endPointId: String, payload: Payload) {
        Nearby.getConnectionsClient(application).sendPayload(endPointId, payload)
    }

    fun send(bytes: ByteArray) {
        CoroutineScope(IO).launch {
            while (!isConnected) {
                reentrantLock.lock()
            }
            sendPayload(strendPointId!!, Payload.fromBytes(bytes))
        }
    }

    fun disconnect() {
        isConnected = false
        INSTANCE = null
        Nearby.getConnectionsClient(application).stopAllEndpoints()
    }

    companion object {
        @Volatile
        private var INSTANCE: Connectivity? = null
        fun getInstance(isClient: Boolean, SERVICE_ID: String, callback: PayloadCallback, context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Connectivity(isClient, SERVICE_ID, callback, context.applicationContext as Application).also { INSTANCE = it }
            }
    }
}