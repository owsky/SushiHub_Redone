package com.owsky.sushihubredone.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.owsky.sushihubredone.data.entities.Table
import kotlinx.coroutines.flow.Flow

@Dao
interface TableDao : BaseDao<Table> {

    @Query("SELECT * FROM `Table` WHERE id = :table")
    suspend fun getTable(table: String): Table?

    @Query("SELECT * FROM `Table` WHERE isCheckedOut")
    fun getAllButCurrent(): Flow<List<Table>>

    @Query("SELECT COALESCE(SUM(menuPrice), 0) FROM `Table` WHERE id = :table")
    suspend fun getMenuPrice(table: String): Double

    @Query("DELETE FROM `Table`")
    suspend fun deleteAllTables()
}