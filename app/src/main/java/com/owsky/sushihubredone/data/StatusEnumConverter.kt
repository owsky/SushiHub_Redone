package com.owsky.sushihubredone.data

import androidx.room.TypeConverter
import com.owsky.sushihubredone.data.entities.OrderStatus

class StatusEnumConverter {
    @TypeConverter
    fun fromStatusEnum(value: OrderStatus): String {
        return value.toString()
    }

    @TypeConverter
    fun toStatusEnum(value: String): OrderStatus {
        return OrderStatus.valueOf(value)
    }
}