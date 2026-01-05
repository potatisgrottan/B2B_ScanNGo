package com.example.b2b_scanngo

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.b2b_scanngo.repositroy.FakeProductRepo
import com.example.b2b_scanngo.ui.screen.*
import com.example.b2b_scanngo.viewModel.MainViewModel

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()

    // Kolla om vi har en inloggad användare för att bestämma startskärm
    val startDestination = if (viewModel.currentUser == null) "login" else "home"

    NavHost(navController = navController, startDestination = startDestination) {

        // --- LOGIN SCREEN ---
        composable("login") {
            LoginScreen(viewModel) {
                // Vid lyckad inloggning, rensa backstack så man inte kan backa till login
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }

        // --- HOME SCREEN ---
        composable("home") {
            HomeScreen(navController)
        }

        // --- ORDER SCREEN ---
        composable("order") {
            OrderScreen(navController, viewModel)
        }

        // --- HISTORY SCREEN ---
        composable("history") {
            HistoryScreen(navController, viewModel)
        }

        // --- SCAN SCREEN ---
        composable("scan") {
            ScanScreen(navController) { eanCode ->
                val productInfo = FakeProductRepo.getProductByEan(eanCode)
                if (productInfo != null) {
                    viewModel.addProduct(eanCode, productInfo)
                    return@ScanScreen true
                } else {
                    return@ScanScreen false
                }
            }
        }
    }
}