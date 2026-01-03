package com.example.b2b_scanngo.model
data class CartItem(
    val name: String,
    val ean: String,
    val price: Int,
    var quantity: Int
)