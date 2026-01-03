package com.example.b2b_scanngo.repositroy

// Vi skapar en enkel klass för att hålla info om produkten
data class ProductInfo(
    val name: String,
    val price: Int
)

object FakeProductRepo {

    // Nu mappar vi EAN-koden till ett ProductInfo-objekt (Namn + Pris)
    private val productDatabase = mapOf(
        "7310532109090" to ProductInfo("Barilla Spaghetti", 20),
        "7310865004703" to ProductInfo("Arla Mellanmjölk 1L", 15),
        "7340083438686" to ProductInfo("Eldorado Krossade Tomater", 12),
        "7622201733704" to ProductInfo("Marabou Daim King size", 25),
        "2340398010068" to ProductInfo("Arla Gräddis ost (kg-pris)", 95),
        "7310865889409" to ProductInfo("Klöver mellanmjölk 1,5L", 18),
        "7318690140382" to ProductInfo("Vispgrädde 2,5dl", 22),
        "7318690075523" to ProductInfo("Baby plommontomater 250g", 35),
        "7318690097235" to ProductInfo("Grekiska oliver", 29),
        "7318690052678" to ProductInfo("Bak smör ica", 50),
        "7311870010970" to ProductInfo("Bregott smör 500g", 65),
        "7311870011458" to ProductInfo("Arla mellan mjölk 2L", 60),
        "7311070008708" to ProductInfo("Grötbröd", 30),
        "7310532196699" to ProductInfo("ranch chips estrella", 68),
        "7318690180104" to ProductInfo("Sirap, Mapel", 60),
        "123456" to ProductInfo("Testprodukt (Skanna mig!)", 500)
    )

    // OBS: Nu returnerar vi ProductInfo? istället för String?
    fun getProductByEan(ean: String): ProductInfo? {
        return productDatabase[ean]
    }
}