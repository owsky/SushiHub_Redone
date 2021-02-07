package com.owsky.sushihubredone.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity
@Parcelize
data class Table(
    @PrimaryKey val id: String,
    var restaurant: String,
    val dateCreation: Date,
    var menuPrice: Double
) : Parcelable {
    @IgnoredOnParcel
    var isCheckedOut = false
}