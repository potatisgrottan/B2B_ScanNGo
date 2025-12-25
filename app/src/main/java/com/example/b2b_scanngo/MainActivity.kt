package com.example.b2b_scanngo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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

    // Eftersom vi har lagt skärmarna i egna filer hittar Android Studio
    // dem automatiskt här (förutsatt att de har samma paketnamn).
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("order") { OrderScreen(navController) }
        composable("scan") { ScanScreen(navController) }
    }
}