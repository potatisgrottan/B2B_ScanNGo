package com.example.b2b_scanngo

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.b2b_scanngo.viewModel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Fråga om lov för notifikationer (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }

        setContent {
            // 2. Skapa ViewModel och initiera NotificationHelper
            val viewModel: MainViewModel = viewModel()
            viewModel.initNotificationHelper(LocalContext.current)

            // 3. Starta appens navigering
            AppNavigation(viewModel)
        }
    }
}