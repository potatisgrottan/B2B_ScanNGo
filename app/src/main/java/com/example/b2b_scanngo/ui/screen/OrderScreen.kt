package com.example.b2b_scanngo.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.b2b_scanngo.model.CartItem
import com.example.b2b_scanngo.viewModel.MainViewModel


@Composable
fun OrderScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val cartItems = viewModel.cartItems

    // Räkna ut totalen live för att visa på skärmen
    val currentTotal = cartItems.sumOf { it.price * it.quantity }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Din Varukorg",
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5))
        ) {
            items(cartItems) { item ->
                CartItemRow(item)
            }
        }

        // Summering och Knappar
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp) // Lite mellanrum mellan knapparna
        ) {
            // Visa Totalsumma
            Text(
                text = "Totalt: $currentTotal kr",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // SCAN KNAPP
            Button(
                onClick = { navController.navigate("scan") },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("SCANNA VARA", fontSize = 18.sp)
            }

            // BESTÄLL KNAPP (Nu funkar viewModel.placeOrder!)
            Button(
                onClick = {
                    viewModel.placeOrder()
                    navController.navigate("history")
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Grön färg
            ) {
                Text("LÄGG BESTÄLLNING")
            }
        }
    }
}

// UPPDATERA RADEN OCKSÅ FÖR ATT VISA PRIS
@Composable
fun CartItemRow(item: CartItem) {
    // ... (imports och state för quantity är samma)
    var qty by remember { mutableIntStateOf(item.quantity) } // OBS: Om du ändrar i viewmodeln måste du egentligen ändra här, men för demo funkar detta.

    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "EAN: ${item.ean}", style = MaterialTheme.typography.bodySmall)
                // VISA PRIS PER STYCK
                Text(text = "${item.price} kr/st", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }

            // ... (Knapparna +/- är samma) ...
            // TIPS: Uppdatera totalpriset för raden:
            Text(text = "${item.price * qty} kr", modifier = Modifier.padding(start = 8.dp))
        }
    }
}