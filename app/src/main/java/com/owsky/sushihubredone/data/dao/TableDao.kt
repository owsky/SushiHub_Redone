package com.owsky.sushihubredone.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.owsky.sushihubredone.data.entities.Table

@Dao
interface TableDao : BaseDao<Table> {

    @Query("SELECT * FROM `Table` WHERE id = :table")
    suspend fun getTable(table: String): Table?

    @Query("SELECT * FROM `Table` WHERE isCheckedOut")
    fun getAllButCurrent(): LiveData<List<Table>>

    @Query("SELECT menuPrice FROM `Table` WHERE id = :table")
    suspend fun getMenuPrice(table: String): Double

    @Query("DELETE FROM `Table`")
    suspend fun deleteAllTables()
}