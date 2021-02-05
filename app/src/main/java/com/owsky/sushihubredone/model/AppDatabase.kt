package com.owsky.sushihubredone.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.owsky.sushihubredone.model.dao.OrderDao
import com.owsky.sushihubredone.model.dao.TableDao
import com.owsky.sushihubredone.model.entities.Order
import com.owsky.sushihubredone.model.entities.Table

@Database(entities = [Order::class, Table::class], exportSchema = false, version = 1)
@TypeConverters(StatusEnumConverter::class, TimestampConverter::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "SushiHub_Redone")
                        .fallbackToDestructiveMigration().build()
            return instance!!
        }
    }

    abstract fun orderDao(): OrderDao
    abstract fun tableDao(): TableDao
}