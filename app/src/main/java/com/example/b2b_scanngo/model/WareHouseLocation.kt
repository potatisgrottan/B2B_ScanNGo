package com.example.b2b_scanngo.model

data class WarehouseLocation(
    val id: String,
    val name: String, // e.g., "Main Warehouse Gate A"
    val streetAddress: String,
    val floor: Int
)