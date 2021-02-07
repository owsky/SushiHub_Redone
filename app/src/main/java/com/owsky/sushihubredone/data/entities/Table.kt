package com.owsky.sushihubredone.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Table(
    @PrimaryKey val id: String,
    var restaurant: String,
    val dateCreation: Date,
    var menuPrice: Double
) {
    var isCheckedOut = false
}