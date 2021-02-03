package com.owsky.sushihubredone.model

import android.content.ContentValues
import android.util.Log
import com.couchbase.lite.CouchbaseLiteException
import com.couchbase.lite.Database
import com.couchbase.lite.MutableDocument
import java.util.*

class DatabaseOp {
	companion object {
		private val database = Database("SushiHub_Redone")

		fun createTable(tableCode: String, tableName: String, menuPrice: Float) {
			val newTable = MutableDocument(tableCode)
			newTable.setString("restaurant", tableName)
			newTable.setFloat("menu_price", menuPrice)
			newTable.setDate("date", Date())
			try {
				database.save(newTable)
			} catch (exception: CouchbaseLiteException) {
				Log.d(ContentValues.TAG, "createTable: $exception")
			}
		}

		fun getTableInfo(tableCode: String): List<String> {
			val table = database.getDocument(tableCode)
			val restaurant = table.getString("restaurant")
			val menuPrice = table.getFloat("menu_price").toString()
			return listOf(restaurant!!, menuPrice)
		}
	}
}