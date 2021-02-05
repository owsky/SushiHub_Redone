package com.owsky.sushihubredone.model

import androidx.room.TypeConverter
import java.util.*

class TimestampConverter {
    @TypeConverter
    fun fromTimeStamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}