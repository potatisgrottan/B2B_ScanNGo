package com.example.b2b_scanngo.model

data class Product(
    val ean: String,
    val name: String,
    val price: Int,
    val supplierName: String,
    val category: String
)