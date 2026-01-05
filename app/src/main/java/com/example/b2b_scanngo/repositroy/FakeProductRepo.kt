package com.example.b2b_scanngo.repositroy

import com.example.b2b_scanngo.model.Product
import com.example.b2b_scanngo.model.WarehouseLocation

object FakeProductRepo { // Keep this name so AppNavigation doesn't break
    val suppliers = listOf("TechLogistics AB", "Global Foods Corp", "Industrial Supplies Ltd", "Vicks")

    val warehouseLocations = listOf(
        WarehouseLocation("LOC1", "Lastkaj Nord", "Industrivägen 1", 1),
        WarehouseLocation("LOC2", "Inlastning Syd", "Industrivägen 1", 1),
        WarehouseLocation("LOC3", "Smågodsplock", "Lagergränd 4", 2)
    )

    private val productDatabase = listOf(
        Product("7310532109090", "Barilla Spaghetti", 20, suppliers[1], "Food"),
        Product("4030300022248", "Double Action, menthol, Sugar Free halstabletter", 25, suppliers[4], "Food"),
        Product("123456", "Safety Vest XL", 150, suppliers[2], "Safety")
    )

    fun getProductByEan(ean: String): Product? {
        return productDatabase.find { it.ean == ean }
    }
}