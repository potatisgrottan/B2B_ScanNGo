package com.example.b2b_scanngo.model


import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

// Denna klass ärver från ImageAnalysis.Analyzer
class BarcodeAnalyzer(
    private val onBarcodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    // Hämtar ML Kits scanner-klient
    private val scanner = BarcodeScanning.getClient()

    @SuppressLint("UnsafeOptInUsageError") // Krävs för att konvertera bilden
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image

        if (mediaImage != null) {
            // Skapa en InputImage som ML Kit förstår, baserat på kamerans rotation
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            // Be ML Kit leta efter koder
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    // Loopa igenom alla koder den hittade (oftast bara en)
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { code ->
                            // Skicka koden vidare till vår funktion!
                            onBarcodeDetected(code)
                        }
                    }
                }
                .addOnFailureListener {
                    // Om det blir fel (t.ex. suddig bild), gör inget speciellt
                }
                .addOnCompleteListener {
                    // VIKTIGT: Stäng bilden så kameran kan ta nästa bildruta
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}