package com.owsky.sushihubredone.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.owsky.sushihubredone.data.dao.OrderDao
import com.owsky.sushihubredone.data.dao.TableDao
import com.owsky.sushihubredone.data.entities.Order
import com.owsky.sushihubredone.data.entities.Table

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