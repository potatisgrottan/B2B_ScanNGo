package com.example.b2b_scanngo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel // VIKTIG IMPORT!
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.b2b_scanngo.repositroy.FakeProductRepo
import com.example.b2b_scanngo.ui.screen.HomeScreen
import com.example.b2b_scanngo.ui.screen.OrderScreen
import com.example.b2b_scanngo.ui.screen.ScanScreen
import com.example.b2b_scanngo.viewModel.MainViewModel

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
    val viewModel: MainViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(navController)
        }

        composable("order") {
            // Vi skickar listan från ViewModeln
            OrderScreen(navController, viewModel.cartItems)
        }

        composable("scan") {
            ScanScreen(navController) { eanCode ->

                val productName = FakeProductRepo.getProductByEan(eanCode)

                if (productName != null) {
                    viewModel.addProduct(eanCode, productName)
                    return@ScanScreen true
                } else {
                    return@ScanScreen false
                }
            }
        }
    }
}