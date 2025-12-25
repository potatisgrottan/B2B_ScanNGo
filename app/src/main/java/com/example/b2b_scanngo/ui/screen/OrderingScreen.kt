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

@Composable
fun OrderScreen(navController: NavController) {
    // Mock-data
    val cartItems = remember { mutableStateListOf(
        CartItem("Krossade Tomater", "73123456", 5),
        CartItem("Olivolja 1L", "73999999", 1),
        CartItem("Pasta Penne 5kg", "73555555", 2)
    )}

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Din Varukorg",
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )

        // DEL 1: LISTAN
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

        // DEL 2: SCAN KNAPP
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = { navController.navigate("scan") },
                modifier = Modifier.size(width = 200.dp, height = 80.dp)
            ) {
                Text("SCAN ITEM", fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItem) {
    var qty by remember { mutableIntStateOf(item.quantity) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "EAN: ${item.ean}", style = MaterialTheme.typography.bodySmall)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { if (qty > 0) qty-- }) {
                    Text("-", fontSize = 24.sp)
                }
                Text(text = "$qty", fontSize = 18.sp, modifier = Modifier.padding(horizontal = 8.dp))
                IconButton(onClick = { qty++ }) {
                    Text("+", fontSize = 24.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderScreenPreview() {
    val navController = rememberNavController()
    OrderScreen(navController = navController)
}