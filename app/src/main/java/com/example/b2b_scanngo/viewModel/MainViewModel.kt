package com.example.b2b_scanngo.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.b2b_scanngo.model.*
import com.example.b2b_scanngo.repositroy.FakeProductRepo
import com.google.firebase.auth.FirebaseAuth // Viktig import!
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel : ViewModel() {

    // --- Firebase Auth ---
    private val auth = FirebaseAuth.getInstance()
    var currentUser by mutableStateOf(auth.currentUser)
    var authError by mutableStateOf<String?>(null)

    // --- Lager & Order ---
    val cartItems = mutableStateListOf<CartItem>()
    val orderHistory = mutableStateListOf<Order>()
    var selectedLocation by mutableStateOf(FakeProductRepo.warehouseLocations[0])

    @SuppressLint("StaticFieldLeak")
    private var notificationHelper: NotificationHelper? = null

    fun initNotificationHelper(context: Context) {
        notificationHelper = NotificationHelper(context)
    }

    // --- Auth Funktioner ---
    fun signIn(email: String, pass: String, onSuccess: () -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                currentUser = it.user
                authError = null
                onSuccess()
            }
            .addOnFailureListener {
                authError = "Inloggning misslyckades: ${it.message}"
            }
    }

    fun signUp(email: String, pass: String, onSuccess: () -> Unit) {
        // Validering för att stoppa kraschen
        if (email.isBlank() || pass.isBlank()) {
            authError = "E-post och lösenord får inte vara tomma"
            return
        }

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                currentUser = it.user
                authError = null
                onSuccess()
            }
            .addOnFailureListener {
                // Här fångas fel som t.ex. för kort lösenord eller ogiltig e-post
                authError = "Registrering misslyckades: ${it.localizedMessage}"
            }
    }

    fun signOut() {
        auth.signOut()
        currentUser = null
    }

    // --- Order & Produkter ---
    fun addProduct(ean: String, info: Product) {
        val existingItem = cartItems.find { it.ean == ean }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            cartItems.add(CartItem(info.name, ean, info.price, 1))
        }
    }

    fun placeOrder() {
        if (cartItems.isEmpty()) return

        val totalSum = cartItems.sumOf { it.price * it.quantity }

        // Vi skapar en order som nu är kopplad till användarens e-post för "Security/Auth"
        val newOrder = Order(
            id = UUID.randomUUID().toString().substring(0, 8).uppercase(),
            items = cartItems.toList(),
            totalPrice = totalSum,
            deliveryAddress = selectedLocation,
            status = OrderStatus.PLACED,
            // Du kan lägga till 'userEmail = currentUser?.email ?: "Gäst"' i din Order-modell
        )

        orderHistory.add(0, newOrder)
        cartItems.clear()
        simulateDeliveryProcess(newOrder.id)
    }

    private fun simulateDeliveryProcess(orderId: String) {
        viewModelScope.launch {
            delay(5000)
            updateOrderStatus(orderId, OrderStatus.ON_THE_WAY)
            notificationHelper?.showNotification("Order #$orderId", "På väg till ${selectedLocation.name}.")

            delay(15000)
            updateOrderStatus(orderId, OrderStatus.DELIVERED)
            notificationHelper?.showNotification("Order #$orderId", "Levererad till ${selectedLocation.name}.")
        }
    }

    private fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        val index = orderHistory.indexOfFirst { it.id == orderId }
        if (index != -1) {
            orderHistory[index] = orderHistory[index].copy(status = newStatus)
        }
    }

    fun markOrderAsCompleted(order: Order) {
        orderHistory.remove(order)
    }
}