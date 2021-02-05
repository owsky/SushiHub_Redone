package com.owsky.sushihubredone.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.owsky.sushihubredone.model.entities.Order
import com.owsky.sushihubredone.model.entities.OrderStatus

@Dao
interface OrderDao : BaseDao<Order> {

    @Query("SELECT * FROM `Order` WHERE tableCode = :code")
    fun getAllByTable(code: String): LiveData<List<Order>>

    @Query("SELECT SUM(price) FROM `Order` WHERE tableCode = :table AND NOT receivedFromSlave")
    suspend fun getExtraPrice(table: String): Double

    @Query("SELECT * FROM `Order` WHERE status = :status AND tableCode = :table AND NOT receivedFromSlave")
    fun getAllByStatus(status: OrderStatus, table: String): LiveData<List<Order>>

    @Query("SELECT * FROM `Order` WHERE tableCode = :table AND status = :status")
    fun getAllSynchronized(table: String, status: OrderStatus = OrderStatus.Confirmed): LiveData<List<Order>>

    @Query("SELECT * FROM `Order` WHERE status = :status AND tableCode = :table AND dish = :dish AND user = :user")
    suspend fun contains(status: OrderStatus, table: String, dish: String, user: String): Order?

    @Query("DELETE FROM `Order` WHERE tableCode = :table")
    suspend fun deleteByTable(table: String)

    @Query("DELETE FROM `Order` WHERE receivedFromSlave")
    suspend fun deleteSlaves()

    @Insert
    suspend fun insertAll(vararg obj: Order): LongArray

    @Query("DELETE FROM `Order` WHERE id in (:id)")
    suspend fun deleteAllById(id: LongArray)
}