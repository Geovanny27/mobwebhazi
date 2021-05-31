package hu.bme.aut.inventoryapp.data

import androidx.room.*

@Dao
interface inventoryItemDao {
    @Query("SELECT * FROM inventoryitem")
    fun getAll(): List<InventoryItem>

    @Insert
    fun insert(inventoryItems: InventoryItem): Long

    @Update
    fun update(inventoryItem: InventoryItem)

    @Delete
    fun deleteItem(inventoryItem: InventoryItem)

}