package com.owsky.sushihubredone.util

interface Connectivity {

    fun sendPayload(byteArray: ByteArray)

    fun disconnect()
}