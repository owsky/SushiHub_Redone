package com.owsky.sushihubredone.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.owsky.sushihubredone.model.DatabaseOp

class CreateTableViewModel(application: Application) : AndroidViewModel(application) {

	fun createTable(tableCode: String, tableName: String, menuPrice: Float) {
		DatabaseOp.createTable(tableCode, tableName, menuPrice)
	}

	fun getTableInfo(tableCode: String): List<String> {
		return DatabaseOp.getTableInfo(tableCode)
	}
}