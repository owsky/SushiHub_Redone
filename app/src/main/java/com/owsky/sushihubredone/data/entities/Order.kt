package com.owsky.sushihubredone.data.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Order(
    val dish: String,
    val desc: String,
    var status: OrderStatus,
    var tableCode: String,
    val user: String,
    val receivedFromSlave: Boolean,
    val price: Double
) : Parcelable {
    companion object : Parceler<Order> {
        override fun create(parcel: Parcel): Order {
            return Order(
                parcel.readString()!!,
                parcel.readString()!!,
                OrderStatus.valueOf(parcel.readString()!!),
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readBoolean(),
                parcel.readDouble()
            )
        }

        override fun Order.write(parcel: Parcel, flags: Int) {
            parcel.apply {
                writeLong(id)
                writeString(dish)
                writeString(desc)
                writeString(status.toString())
                writeString(tableCode)
                writeString(user)
                writeBoolean(receivedFromSlave)
                writeDouble(price)
            }

        }

        private fun marshall(parcelable: Parcelable): ByteArray {
            val parcel = Parcel.obtain()
            parcelable.writeToParcel(parcel, 0)
            val bytes = parcel.marshall()
            parcel.recycle()
            return bytes
        }

        private fun unMarshall(bytes: ByteArray): Parcel {
            val parcel = Parcel.obtain()
            parcel.unmarshall(bytes, 0, bytes.size)
            parcel.setDataPosition(0)
            return parcel
        }

        fun fromByteArray(bytes: ByteArray): Order {
            return create(unMarshall(bytes))
        }

        fun toByteArray(order: Order): ByteArray {
            return marshall(order)
        }
    }

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

enum class OrderStatus {
    Pending,
    Confirmed,
    Delivered,
    InsertOrder,
    DeliverOrder,
    UndoDeliverOrder,
    DeleteOrder;
}