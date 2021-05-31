package hu.bme.aut.inventoryapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import hu.bme.aut.shoppinglist.data.ShoppingListItem

@Database(entities = [ShoppingListItem::class], version = 1)
abstract class ShoppingListDatabase : RoomDatabase() {
    abstract fun shoppingListItemDao(): ShoppingListItemDao
}