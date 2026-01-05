package com.example.b2b_scanngo.viewModel

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.b2b_scanngo.model.CartItem
import com.example.b2b_scanngo.model.NotificationHelper
import com.example.b2b_scanngo.model.Order
import com.example.b2b_scanngo.model.OrderStatus
import com.example.b2b_scanngo.model.Product // Updated import
import com.example.b2b_scanngo.model.WarehouseLocation // New import
import com.example.b2b_scanngo.repositroy.FakeProductRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel : ViewModel() {

    val cartItems = mutableStateListOf<CartItem>()
    val orderHistory = mutableStateListOf<Order>()

    // 1. ADDED: State for the currently selected delivery address
    // This defaults to the first location in your "larger" database
    var selectedLocation by mutableStateOf(FakeProductRepo.warehouseLocations[0])

    @SuppressLint("StaticFieldLeak")
    private var notificationHelper: NotificationHelper? = null

    fun initNotificationHelper(context: Context) {
        notificationHelper = NotificationHelper(context)
    }

    // 2. UPDATED: Parameter changed from ProductInfo to Product
    fun addProduct(ean: String, info: Product) {
        val existingItem = cartItems.find { it.ean == ean }

        if (existingItem != null) {
            existingItem.quantity++
        } else {
            // Added with quantity 1 and the price from the product database
            cartItems.add(CartItem(info.name, ean, info.price, 1))
        }
    }

    // 3. UPDATED: Order now includes the deliveryAddress
    fun placeOrder() {
        if (cartItems.isEmpty()) return

        val totalSum = cartItems.sumOf { it.price * it.quantity }

        val newOrder = Order(
            id = UUID.randomUUID().toString().substring(0, 8).uppercase(),
            items = cartItems.toList(),
            totalPrice = totalSum,
            deliveryAddress = selectedLocation, // <--- COWORKER REQUEST: Addressing added here
            status = OrderStatus.PLACED
        )

        orderHistory.add(0, newOrder)
        cartItems.clear()

        simulateDeliveryProcess(newOrder.id)
    }

    private fun simulateDeliveryProcess(orderId: String) {
        viewModelScope.launch {
            // Wait 5 seconds to simulate warehouse picking
            delay(5000)
            updateOrderStatus(orderId, OrderStatus.ON_THE_WAY)
            notificationHelper?.showNotification(
                "Order #$orderId är på väg!",
                "Din leverans är på väg till ${selectedLocation.name}."
            )

            // Wait 15 seconds to simulate driving/delivery
            delay(15000)
            updateOrderStatus(orderId, OrderStatus.DELIVERED)
            notificationHelper?.showNotification(
                "Order #$orderId har anlänt!",
                "Varorna finns nu vid ${selectedLocation.name}. Vänligen kvittera."
            )
        }
    }

    private fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        val index = orderHistory.indexOfFirst { it.id == orderId }
        if (index != -1) {
            val updatedOrder = orderHistory[index].copy(status = newStatus)
            orderHistory[index] = updatedOrder
        }
    }

    fun markOrderAsCompleted(order: Order) {
        orderHistory.remove(order)
    }
}