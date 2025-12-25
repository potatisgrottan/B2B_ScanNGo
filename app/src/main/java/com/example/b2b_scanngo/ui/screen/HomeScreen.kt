package com.example.b2b_scanngo.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        Button(
            onClick = { navController.navigate("order") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text(text = "Start Ordering", fontSize = 20.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // Vi skapar en "tom" navController bara för att previewen ska funka
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}