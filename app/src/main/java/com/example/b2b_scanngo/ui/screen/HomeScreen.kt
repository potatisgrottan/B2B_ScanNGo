package com.example.b2b_scanngo.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun HomeScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Vi använder en Column för att stapla knapparna vertikalt
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp), // 20dp mellanrum mellan knapparna
            modifier = Modifier.padding(16.dp)
        ) {

            // KNAPP 1: Starta beställning (Stor och tydlig)
            Button(
                onClick = { navController.navigate("order") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp) // Lite högre för att vara extra tydlig
            ) {
                Text(text = "START ORDERING", fontSize = 24.sp)
            }

            // KNAPP 2: Visa historik
            // Jag använder OutlinedButton här för att visa att det är en "sekundär" funktion,
            // det ser ofta snyggare ut än två identiska knappar.
            OutlinedButton(
                onClick = { navController.navigate("history") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(text = "VISA HISTORIK", fontSize = 18.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}