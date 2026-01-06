package com.example.b2b_scanngo.viewModel

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.b2b_scanngo.model.*
import com.example.b2b_scanngo.repositroy.FakeProductRepo
import com.google.firebase.auth.FirebaseAuth
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

    // --- Auth Funktioner (Samma som förut) ---
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
                authError = "Registrering misslyckades: ${it.localizedMessage}"
            }
    }

    fun signOut() {
        auth.signOut()
        currentUser = null
    }

    // --- Order & Produkter ---

    fun addProduct(ean: String, info: Product) {
        val index = cartItems.indexOfFirst { it.ean == ean }
        if (index != -1) {
            // Vi använder copy() för att tvinga UI att uppdateras
            val currentItem = cartItems[index]
            cartItems[index] = currentItem.copy(quantity = currentItem.quantity + 1)
        } else {
            cartItems.add(CartItem(info.name, ean, info.price, 1))
        }
    }

    // NY FUNKTION: Öka antal
    fun increaseQuantity(item: CartItem) {
        val index = cartItems.indexOf(item)
        if (index != -1) {
            val current = cartItems[index]
            cartItems[index] = current.copy(quantity = current.quantity + 1)
        }
    }

    // NY FUNKTION: Minska antal (ta bort om 0)
    fun decreaseQuantity(item: CartItem) {
        val index = cartItems.indexOf(item)
        if (index != -1) {
            val current = cartItems[index]
            if (current.quantity > 1) {
                cartItems[index] = current.copy(quantity = current.quantity - 1)
            } else {
                removeItem(item)
            }
        }
    }

    // NY FUNKTION: Ta bort helt
    fun removeItem(item: CartItem) {
        cartItems.remove(item)
    }

    fun placeOrder() {
        if (cartItems.isEmpty()) return

        val totalSum = cartItems.sumOf { it.price * it.quantity }

        val newOrder = Order(
            id = UUID.randomUUID().toString().substring(0, 8).uppercase(),
            items = cartItems.toList(), // Kopia av listan just nu
            totalPrice = totalSum,
            deliveryAddress = selectedLocation,
            status = OrderStatus.PLACED,
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