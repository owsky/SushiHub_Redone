package com.owsky.sushihubredone.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class CreateTableViewModel(application: Application) : AndroidViewModel(application) {
//	repo
	init {
//		repo init
	}

//	createTable for the master
	fun createTable(name: String, menuPrice: Float) {
//		repo.createTable(id, name, menuPrice)
	}

//	createTable fore the slaves
	fun createTable(name: String, Code: String, menuPrice: Float) {
//		repo.createTable(id, name, menuPrice)
	}

	fun getTableInfo(): List<String> {
//		return repo.getTableInfo()
		return listOf("memes")
	}
}