package com.example.b2b_scanngo.ui.screen


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.b2b_scanngo.model.Order
import com.example.b2b_scanngo.model.OrderStatus
import com.example.b2b_scanngo.viewModel.MainViewModel

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Orderhistorik", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(viewModel.orderHistory) { order ->
                OrderHistoryItem(order, onDelete = {
                    viewModel.markOrderAsCompleted(order)
                })
            }
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Tillbaka")
        }
    }
}

@Composable
fun OrderHistoryItem(order: Order, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (order.status == OrderStatus.DELIVERED) Color(0xFFE8F5E9) else Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order ID: ${order.id}", style = MaterialTheme.typography.titleMedium)
            Text("Status: ${order.status}", style = MaterialTheme.typography.bodyMedium)
            Text("Totalt: ${order.totalPrice} kr")

            if (order.status == OrderStatus.DELIVERED) {
                Button(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Kvittera & Ta bort")
                }
            }
        }
    }
}