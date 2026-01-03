package com.example.b2b_scanngo



import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.b2b_scanngo.repositroy.FakeProductRepo
import com.example.b2b_scanngo.ui.screen.HistoryScreen
import com.example.b2b_scanngo.ui.screen.HomeScreen
import com.example.b2b_scanngo.ui.screen.OrderScreen
import com.example.b2b_scanngo.ui.screen.ScanScreen
import com.example.b2b_scanngo.viewModel.MainViewModel

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(navController)
        }

        composable("order") {
            OrderScreen(navController, viewModel)
        }

        composable("history") {
            HistoryScreen(navController, viewModel)
        }

        composable("scan") {
            ScanScreen(navController) { eanCode ->
                // Hämta info (Namn + Pris)
                val productInfo = FakeProductRepo.getProductByEan(eanCode)

                if (productInfo != null) {
                    // Skicka info till ViewModel
                    viewModel.addProduct(eanCode, productInfo)
                    return@ScanScreen true
                } else {
                    return@ScanScreen false
                }
            }
        }
    }
}