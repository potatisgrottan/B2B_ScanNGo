package com.example.b2b_scanngo.model
// Detta är vår datamodell. Eftersom den ligger i en egen fil
// kan den användas överallt i appen.
data class CartItem(
    val name: String,
    val ean: String,
    var quantity: Int
)