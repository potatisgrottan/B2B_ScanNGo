package com.example.b2b_scanngo.viewModel

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.b2b_scanngo.model.CartItem
import com.example.b2b_scanngo.model.NotificationHelper
import com.example.b2b_scanngo.model.Order
import com.example.b2b_scanngo.model.OrderStatus
import com.example.b2b_scanngo.repositroy.ProductInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel : ViewModel() {

    val cartItems = mutableStateListOf<CartItem>()

    // Vår "Databas" för historik
    val orderHistory = mutableStateListOf<Order>()

    // Notification Helper (Måste initieras från MainActivity egentligen, men vi gör en ful-fix för demo)
    @SuppressLint("StaticFieldLeak")
    private var notificationHelper: NotificationHelper? = null

    fun initNotificationHelper(context: Context) {
        notificationHelper = NotificationHelper(context)
    }

    fun addProduct(ean: String, info: ProductInfo) {
        val existingItem = cartItems.find { it.ean == ean }

        if (existingItem != null) {
            existingItem.quantity++
        } else {
            // Här lägger vi in priset från databasen
            cartItems.add(CartItem(info.name, ean, 1, info.price))
        }
    }

    fun placeOrder() {
        if (cartItems.isEmpty()) return

        val totalSum = cartItems.sumOf { it.price * it.quantity }

        val newOrder = Order(
            id = UUID.randomUUID().toString().substring(0, 8).uppercase(),
            items = cartItems.toList(),
            totalPrice = totalSum, // <--- SKICKA MED SUMMAN
            status = OrderStatus.PLACED
        )

        orderHistory.add(0, newOrder)

        cartItems.clear()


        simulateDeliveryProcess(newOrder.id)
    }

    private fun simulateDeliveryProcess(orderId: String) {
        viewModelScope.launch {

            delay(5000)
            updateOrderStatus(orderId, OrderStatus.ON_THE_WAY)
            notificationHelper?.showNotification(
                "Order #$orderId är på väg!",
                "Din leverans har lämnat lagret."
            )

            delay(15000)
            updateOrderStatus(orderId, OrderStatus.DELIVERED)
            notificationHelper?.showNotification(
                "Order #$orderId har anlänt!",
                "Varorna finns nu vid lastkajen. Vänligen kvittera."
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