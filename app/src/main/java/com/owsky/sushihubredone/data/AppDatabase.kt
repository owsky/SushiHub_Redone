package com.owsky.sushihubredone.data

import android.content.Context
import androidx.room.*
import com.owsky.sushihubredone.data.dao.OrderDao
import com.owsky.sushihubredone.data.dao.TableDao
import com.owsky.sushihubredone.data.entities.Order
import com.owsky.sushihubredone.data.entities.OrderStatus
import com.owsky.sushihubredone.data.entities.Table
import java.util.*

@Database(entities = [Order::class, Table::class], exportSchema = false, version = 1)
@TypeConverters(StatusEnumConverter::class, TimestampConverter::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) { instance ?: buildDatabase(context).also { instance = it } }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, AppDatabase::class.java, "SushiHub_Redone").fallbackToDestructiveMigration().build()
    }

    abstract fun orderDao(): OrderDao
    abstract fun tableDao(): TableDao
}

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