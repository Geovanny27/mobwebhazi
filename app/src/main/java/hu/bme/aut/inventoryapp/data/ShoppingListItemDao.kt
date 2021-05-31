package hu.bme.aut.inventoryapp.data

import androidx.room.*
import hu.bme.aut.shoppinglist.data.ShoppingListItem

@Dao
interface ShoppingListItemDao {
    @Query("SELECT * FROM shoppingitem")
    fun getAll(): List<ShoppingListItem>

    @Insert
    fun insert(shoppingListItems: ShoppingListItem): Long

    @Update
    fun update(shoppingListItem: ShoppingListItem)

    @Delete
    fun deleteItem(shoppingListItem: ShoppingListItem)
}