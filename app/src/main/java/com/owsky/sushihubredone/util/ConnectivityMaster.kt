package com.owsky.sushihubredone.util

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.Strategy

class ConnectivityMaster(private val context: Context, private val connectionLifecycleCallback: ConnectionLifecycleCallback) : Connectivity {
    private val serviceId = context.getSharedPreferences("SushiHub_Redone", Context.MODE_PRIVATE).getString("table_code", null)

    init {
        startAdvertising()
    }

    private fun startAdvertising() {
        serviceId?.let {
            val advertisingOptions = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
            Nearby.getConnectionsClient(context).startAdvertising("Master", it, connectionLifecycleCallback, advertisingOptions)
        }
    }

    override fun sendPayload(byteArray: ByteArray) {
        // noop
    }

    override fun disconnect() {
        Nearby.getConnectionsClient(context).stopAllEndpoints()
    }
}