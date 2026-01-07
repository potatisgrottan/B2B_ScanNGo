package com.example.b2b_scanngo.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    // Returns a live stream of data. Whenever the DB changes, this updates automatically!
    @Query("SELECT * FROM cart_items")
    fun getAllItems(): Flow<List<CartItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: CartItem)

    @Delete
    suspend fun delete(item: CartItem)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}