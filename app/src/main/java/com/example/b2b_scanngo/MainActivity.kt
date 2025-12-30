package com.example.b2b_scanngo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.b2b_scanngo.model.CartItem
import com.example.b2b_scanngo.repositroy.FakeProductRepo
import com.example.b2b_scanngo.ui.screen.HomeScreen
import com.example.b2b_scanngo.ui.screen.OrderScreen
import com.example.b2b_scanngo.ui.screen.ScanScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // 1. Vi skapar listan HÄR uppe så den lever så länge appen är igång
    val cartItems = remember { mutableStateListOf<CartItem>() }

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(navController)
        }

        composable("order") {
            // 2. Vi skickar listan TILL OrderScreen så den kan visa den
            OrderScreen(navController, cartItems)
        }

        composable("scan") {
            // 3. Vi skickar en funktion till ScanScreen: "Vad ska hända när vi hittar en kod?"
            ScanScreen(navController) { eanCode ->

                // HÄR SKER "API-ANROPET"
                val productName = FakeProductRepo.getProductByEan(eanCode)

                if (productName != null) {
                    // Kolla om varan redan finns i listan
                    val existingItem = cartItems.find { it.ean == eanCode }

                    if (existingItem != null) {
                        // Om den finns, öka antal
                        existingItem.quantity++
                    } else {
                        // Om den är ny, lägg till i listan
                        cartItems.add(CartItem(productName, eanCode, 1))
                    }
                    return@ScanScreen true // Det lyckades!
                } else {
                    return@ScanScreen false // Produkten fanns inte
                }
            }
        }
    }

}