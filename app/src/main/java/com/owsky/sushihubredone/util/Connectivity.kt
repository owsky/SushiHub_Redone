package com.owsky.sushihubredone.util

import android.app.Application
import android.content.SharedPreferences
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import javax.inject.Inject

class Connectivity @Inject constructor(
    private val application: Application,
    prefs: SharedPreferences,
    private val connectionLifecycleCallback: ConnectionLifecycleCallback?,
    private val endpointDiscoveryCallback: EndpointDiscoveryCallback?
) {
    private val serviceId = prefs.getString("table_code", null)

    init {
        if (prefs.contains("is_master"))
            startAdvertising()
        else
            startDiscovery()
    }

    private fun startDiscovery() {
        serviceId?.let {
            val discoveryOptions = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
            Nearby.getConnectionsClient(application).startDiscovery(it, endpointDiscoveryCallback!!, discoveryOptions)
        }
    }

    private fun startAdvertising() {
        serviceId?.let {
            val advertisingOptions = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
            Nearby.getConnectionsClient(application).startAdvertising("Master", it, connectionLifecycleCallback!!, advertisingOptions)
        }
    }

    fun sendPayload(byteArray: ByteArray) {
        endPointId?.let { Nearby.getConnectionsClient(application).sendPayload(it, Payload.fromBytes(byteArray)) }
    }

    companion object {
        var endPointId: String? = null

        fun disconnect(application: Application) {
            Nearby.getConnectionsClient(application).stopAllEndpoints()
            endPointId = null
        }
    }
}