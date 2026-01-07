package com.example.b2b_scanngo.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.b2b_scanngo.model.*
import com.example.b2b_scanngo.repositroy.FakeProductRepo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

// CHANGED: Inherit form AndroidViewModel to get 'application' context for Database
class MainViewModel(application: Application) : AndroidViewModel(application) {

    // --- Room Database Setup ---
    private val db = AppDatabase.getDatabase(application)
    private val cartDao = db.cartDao()

    // --- Firebase Auth ---
    private val auth = FirebaseAuth.getInstance()
    var currentUser by mutableStateOf(auth.currentUser)
    var authError by mutableStateOf<String?>(null)

    // --- Lager & Order ---
    // This list will now automatically sync with the Database
    val cartItems = mutableStateListOf<CartItem>()
    val orderHistory = mutableStateListOf<Order>()
    var selectedLocation by mutableStateOf(FakeProductRepo.warehouseLocations[0])

    @SuppressLint("StaticFieldLeak")
    private var notificationHelper: NotificationHelper? = null

    init {
        // Start listening to the Database immediately
        viewModelScope.launch {
            cartDao.getAllItems().collect { items ->
                cartItems.clear()
                cartItems.addAll(items)
            }
        }
    }

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

    // --- Order & Produkter (UPDATED FOR ROOM) ---

    fun addProduct(ean: String, info: Product) {
        viewModelScope.launch {
            // Check if item exists in list
            val existing = cartItems.find { it.ean == ean }
            if (existing != null) {
                // Update DB
                cartDao.insertOrUpdate(existing.copy(quantity = existing.quantity + 1))
            } else {
                // Insert new into DB
                cartDao.insertOrUpdate(CartItem(info.name, ean, info.price, 1))
            }
        }
    }

    fun increaseQuantity(item: CartItem) {
        viewModelScope.launch {
            cartDao.insertOrUpdate(item.copy(quantity = item.quantity + 1))
        }
    }

    fun decreaseQuantity(item: CartItem) {
        viewModelScope.launch {
            if (item.quantity > 1) {
                cartDao.insertOrUpdate(item.copy(quantity = item.quantity - 1))
            } else {
                cartDao.delete(item)
            }
        }
    }

    fun removeItem(item: CartItem) {
        viewModelScope.launch {
            cartDao.delete(item)
        }
    }

    // --- NEW FEATURE: Re-order ---
    fun reOrder(order: Order) {
        viewModelScope.launch {
            // 1. Clear current cart
            cartDao.clearCart()

            // 2. Add all items from history to DB
            order.items.forEach { item ->
                cartDao.insertOrUpdate(item)
            }
        }
    }

    fun placeOrder() {
        if (cartItems.isEmpty()) return

        val totalSum = cartItems.sumOf { it.price * it.quantity }

        val newOrder = Order(
            id = UUID.randomUUID().toString().substring(0, 8).uppercase(),
            items = cartItems.toList(),
            totalPrice = totalSum,
            deliveryAddress = selectedLocation,
            status = OrderStatus.PLACED,
        )

        orderHistory.add(0, newOrder)

        // IMPORTANT: Clear the offline database after placing order
        viewModelScope.launch {
            cartDao.clearCart()
        }

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