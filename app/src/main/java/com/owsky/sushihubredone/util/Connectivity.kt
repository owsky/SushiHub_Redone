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
    private val isClient: Boolean,
    private val SERVICE_ID: String,
    private val payloadCallback: PayloadCallback,
) {
    private val reentrantLock = ReentrantLock()
    private var endPointId: String? = null
    private val connectionsClient = Nearby.getConnectionsClient(application)

    init {
        if (isClient)
            startDiscovery()
        else
            startAdvertising()
    }

    private fun startAdvertising() {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(strategy).build()
        connectionsClient.startAdvertising("Device A", SERVICE_ID, object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endPointId: String, connectionInfo: ConnectionInfo) {
                connectionsClient.acceptConnection(endPointId, payloadCallback)
            }

            override fun onConnectionResult(p0: String, p1: ConnectionResolution) {
            }

            override fun onDisconnected(p0: String) {}
        }, advertisingOptions)
    }

    private fun startDiscovery() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(strategy).build()
        connectionsClient.startDiscovery(SERVICE_ID, object : EndpointDiscoveryCallback() {

            override fun onEndpointFound(endpointId: String, discoveredEndpointInfo: DiscoveredEndpointInfo) {
                connectionsClient.requestConnection("Device B", endpointId, object : ConnectionLifecycleCallback() {

                    override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                    }

                    override fun onConnectionResult(endPointId: String, connectionResolution: ConnectionResolution) {
                        if (connectionResolution.status.statusCode == ConnectionsStatusCodes.STATUS_OK) {
                            this@Connectivity.endPointId = endPointId
                            isConnected = true
                            reentrantLock.unlock()
                        }
                    }

                    override fun onDisconnected(s: String) {
                        isConnected = false
                        startDiscovery()
                    }
                })
            }

            override fun onEndpointLost(p0: String) {
                isConnected = false
            }
        }, discoveryOptions)
    }

    fun send(bytes: ByteArray) {
        if (!isClient)
            throw WrongRoleException()
        CoroutineScope(IO).launch {
            while (!isConnected) {
                reentrantLock.lock()
            }
            connectionsClient.sendPayload(endPointId!!, Payload.fromBytes(bytes))
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: Connectivity? = null

        @Volatile
        private var isConnected: Boolean = false
        private lateinit var application: Application
        private val strategy = Strategy.P2P_STAR

        fun getInstance(isClient: Boolean, SERVICE_ID: String, callback: PayloadCallback, context: Context) =
            INSTANCE ?: synchronized(this) {
                application = context.applicationContext as Application
                INSTANCE ?: Connectivity(isClient, SERVICE_ID, callback).also { INSTANCE = it }
            }

        fun disconnect() {
            Nearby.getConnectionsClient(application).stopAllEndpoints()
            isConnected = false
            INSTANCE = null
        }
    }
}

class WrongRoleException : Exception() {
    override val message: String
        get() = "Only slaves should transmit orders"
}
