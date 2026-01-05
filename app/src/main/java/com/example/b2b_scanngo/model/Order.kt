package com.example.b2b_scanngo.model

data class Order(
    val id: String,
    val items: List<CartItem>,
    val totalPrice: Int,
    val deliveryAddress: WarehouseLocation,
    var status: OrderStatus = OrderStatus.PLACED,
    val timestamp: Long = System.currentTimeMillis()
)

enum class OrderStatus {
    PLACED, ON_THE_WAY, DELIVERED, COMPLETED
}