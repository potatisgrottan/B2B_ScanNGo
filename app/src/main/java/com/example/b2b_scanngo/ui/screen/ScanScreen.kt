package com.example.b2b_scanngo.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.b2b_scanngo.model.BarcodeAnalyzer
import java.util.concurrent.Executors

@Composable
fun ScanScreen(navController: NavController,
               onProductFound: (String) -> Boolean) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Förhindra att den skannar samma kod 100 gånger på en sekund
    var isScanning by remember { mutableStateOf(true) }

    // Tillstånd för kameran
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher för att fråga om lov
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    // Fråga om lov direkt när skärmen startar om vi inte har det
    LaunchedEffect(key1 = true) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    // ... (PreviewView setup samma som förut) ...

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()

                        // HÄR ÄR LOGIKEN:
                        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(),
                            BarcodeAnalyzer { ean ->

                                // Om vi redan håller på att bearbeta en kod, gör inget
                                if (!isScanning) return@BarcodeAnalyzer

                                // Stoppa skanning tillfälligt
                                isScanning = false

                                // Gå till Main Thread för att göra UI-grejer
                                previewView.post {
                                    val success =
                                        onProductFound(ean) // <--- Skicka koden till MainActivity!

                                    if (success) {
                                        Toast.makeText(
                                            ctx,
                                            "Lade till vara: $ean",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        // Gå tillbaka till listan automatiskt
                                        navController.popBackStack()
                                    } else {
                                        Toast.makeText(
                                            ctx,
                                            "Okänd produkt: $ean",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        // Starta skanningen igen efter 2 sekunder om det blev fel
                                        previewView.postDelayed({ isScanning = true }, 2000)
                                    }
                                }
                            })

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                imageAnalysis
                            )
                        } catch (e: Exception) {
                            Log.e("SCANNER", "Kunde inte starta kamera", e)
                        }

                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Tillfällig knapp för att gå tillbaka manuellt
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
        ) {
            Text("Avbryt")
        }
    }
}

/*@Preview(showBackground = true)
@Composable
fun ScanScreenPreview() {
    val navController = rememberNavController()

    // Skicka med en "tom" funktion som bara returnerar true
    // { _ -> true } betyder: "Jag bryr mig inte om vad in-parametern är, jag returnerar bara true"
    ScanScreen(navController = navController, onProductFound = { _ -> true })
}*/