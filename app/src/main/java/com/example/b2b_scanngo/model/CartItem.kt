package com.example.b2b_scanngo.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val ean: String, // EAN is unique, perfect primary key
    val name: String,
    val price: Int,
    var quantity: Int
)