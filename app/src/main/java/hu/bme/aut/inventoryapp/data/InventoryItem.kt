package hu.bme.aut.inventoryapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "inventoryitem")
data class InventoryItem(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "amount") var amount: Double,
    @ColumnInfo(name = "unit") val unit: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "expiry") val expiry:String
) {

}