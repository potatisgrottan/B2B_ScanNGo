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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.b2b_scanngo.model.CartItem
import com.example.b2b_scanngo.repositroy.FakeProductRepo
import com.example.b2b_scanngo.viewModel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val cartItems = viewModel.cartItems
    val currentTotal = cartItems.sumOf { it.price * it.quantity }

    // State for the dropdown menu
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Din Varukorg",
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )

        // Cart List
        LazyColumn(
            modifier = Modifier
                .weight(1.5f) // Adjusted weight to make room for the picker
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5))
        ) {
            items(cartItems) { item ->
                CartItemRow(item)
            }
        }

        // --- ADDRESS PICKER SECTION ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White)
        ) {
            Text(
                text = "Leveransadress / Lastkaj:",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )

            // Material 3 Dropdown Menu
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.selectedLocation.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Välj fysisk adress") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    FakeProductRepo.warehouseLocations.forEach { location ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(location.name)
                                    Text(location.streetAddress, style = MaterialTheme.typography.bodySmall)
                                }
                            },
                            onClick = {
                                viewModel.selectedLocation = location
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
        // --- END ADDRESS PICKER ---

        // Summering och Knappar
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Totalt: $currentTotal kr",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Button(
                onClick = { navController.navigate("scan") },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("SCANNA VARA", fontSize = 18.sp)
            }

            Button(
                onClick = {
                    if (cartItems.isNotEmpty()) {
                        viewModel.placeOrder()
                        navController.navigate("history")
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = cartItems.isNotEmpty(), // Disable button if cart is empty
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("LÄGG BESTÄLLNING")
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItem) {
    // Note: In a real app, use viewModel functions to change qty
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "Antal: ${item.quantity} st", style = MaterialTheme.typography.bodyMedium)
                Text(text = "${item.price} kr/st", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Text(
                text = "${item.price * item.quantity} kr",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}