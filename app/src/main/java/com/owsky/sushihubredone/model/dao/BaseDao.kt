package com.owsky.sushihubredone.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

@Dao
interface BaseDao<T> {
	@Insert
	suspend fun insert(obj: T)

	@Update
	suspend fun update(obj: T)

	@Delete
	suspend fun delete(obj: T)
}