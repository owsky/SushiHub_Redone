package com.owsky.sushihubredone.util

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

class ConnectivitySlave(private val context: Context, private val endpointDiscoveryCallback: EndpointDiscoveryCallback) : Connectivity{
    private val serviceId = context.getSharedPreferences("SushiHub_Redone", Context.MODE_PRIVATE).getString("table_code", null)

    init {
        startDiscovery()
    }

    private fun startDiscovery() {
        serviceId?.let {
            val discoveryOptions = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
            Nearby.getConnectionsClient(context).startDiscovery(it, endpointDiscoveryCallback, discoveryOptions)
        }
    }

    override fun sendPayload(byteArray: ByteArray) {
        endPointId?.let { Nearby.getConnectionsClient(context).sendPayload(it, Payload.fromBytes(byteArray)) }
    }

    companion object {
        var endPointId: String? = null
    }

    override fun disconnect() {
        Nearby.getConnectionsClient(context).stopAllEndpoints()
    }
}