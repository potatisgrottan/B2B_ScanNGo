package com.example.b2b_scanngo.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.b2b_scanngo.model.CartItem

class MainViewModel : ViewModel() {

    // Vi använder mutableStateListOf inuti ViewModeln
    // Eftersom ViewModel överlever rotation, kommer denna lista finnas kvar!
    val cartItems = mutableStateListOf<CartItem>()

    // Vi flyttar logiken hit också (Clean Architecture)
    fun addProduct(ean: String, name: String) {
        val existingItem = cartItems.find { it.ean == ean }

        if (existingItem != null) {
            existingItem.quantity++
        } else {
            cartItems.add(CartItem(name, ean, 1))
        }
    }
}