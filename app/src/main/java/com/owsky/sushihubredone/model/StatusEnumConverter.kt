package com.owsky.sushihubredone.model

import androidx.room.TypeConverter
import com.owsky.sushihubredone.model.entities.OrderStatus

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