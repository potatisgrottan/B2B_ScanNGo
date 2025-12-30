package com.example.b2b_scanngo.repositroy



object FakeProductRepo {
    // Detta simulerar din databas/API.
    // I verkligheten hade du gjort ett API-anrop här.
    private val productDatabase = mapOf(
        "7310532109090" to "Barilla Spaghetti",
        "7310865004703" to "Arla Mellanmjölk",
        "7340083438686" to "Eldorado Krossade Tomater",
        "7622201733704" to "Marabou Daim King size",
        "2340398010068" to "Arla Gräddis ost",
        "7310865889409" to "Klöver mellan mjölk 1,5L",
        "7318690140382" to "Visp grädde 2,5dl",
        "7318690075523" to "Baby plommon tomater 250g",
        "7318690097235" to "Grekiska oliver",
        "123456" to "Testprodukt (Skanna mig!)"
        // Tips: Du kan generera en streckkod för "123456" på nätet för att testa
    )

    fun getProductByEan(ean: String): String? {
        // Returnerar namnet om det finns, annars null
        return productDatabase[ean]
    }
}