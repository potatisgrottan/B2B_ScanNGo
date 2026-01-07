package com.example.b2b_scanngo.utils

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import com.example.b2b_scanngo.model.Order
import java.io.File
import java.io.FileOutputStream
import java.util.Date

object PdfGenerator {
    fun generateInvoicePdf(context: Context, order: Order) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        // Title
        paint.textSize = 24f
        canvas.drawText("Faktura / Kvitto", 50f, 50f, paint)

        // Order Details
        paint.textSize = 14f
        canvas.drawText("Order ID: ${order.id}", 50f, 100f, paint)
        canvas.drawText("Datum: ${Date(order.timestamp)}", 50f, 120f, paint)
        canvas.drawText("Leverans: ${order.deliveryAddress.name}", 50f, 140f, paint)

        // Items Header
        var y = 180f
        paint.isFakeBoldText = true
        canvas.drawText("Produkt", 50f, y, paint)
        canvas.drawText("Antal", 300f, y, paint)
        canvas.drawText("Pris", 400f, y, paint)

        paint.isFakeBoldText = false
        y += 30f

        // Items List
        order.items.forEach { item ->
            canvas.drawText(item.name, 50f, y, paint)
            canvas.drawText(item.quantity.toString(), 300f, y, paint)
            canvas.drawText("${item.price * item.quantity} kr", 400f, y, paint)
            y += 25f
        }

        // Total
        y += 20f
        paint.isFakeBoldText = true
        paint.textSize = 16f
        canvas.drawText("TOTALT: ${order.totalPrice} kr", 400f, y, paint)

        pdfDocument.finishPage(page)

        // Save file
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "Order_${order.id}.pdf"
        )

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(context, "PDF sparad i Nedladdningar!", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Fel vid PDF-skapande: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            pdfDocument.close()
        }
    }
}