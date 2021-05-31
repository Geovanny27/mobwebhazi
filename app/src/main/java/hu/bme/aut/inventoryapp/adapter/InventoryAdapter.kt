package hu.bme.aut.inventoryapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.inventoryapp.R
import hu.bme.aut.inventoryapp.data.InventoryItem
import kotlin.math.abs

class InventoryAdapter(private val listener: InventoryItemClickListener) :
    RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    private val items = mutableListOf<InventoryItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_inventory, parent, false)
        return InventoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val item = items[position]
        holder.nameTextView.text = item.name
        var amount:String
        if(item.amount==item.amount.toInt().toDouble())
            amount=item.amount.toInt().toString()+" "+item.unit
        else
            amount=item.amount.toString()+" "+item.unit
        holder.amountTextView.text = amount
        holder.categoryTextView.text = item.category
        holder.expiryTextView.text=item.expiry
        holder.item = item
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface InventoryItemClickListener {
        fun onItemChanged(item: InventoryItem)
        fun onItemDeleted(item: InventoryItem)
        fun onItemSelected(item: InventoryItem?)
    }

    fun addItem(item: InventoryItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun update(inventoryItems: List<InventoryItem>) {
        items.clear()
        items.addAll(inventoryItems)
        notifyDataSetChanged()
    }

    fun deleteItem(item: InventoryItem?){
        val idx= items.indexOf(element = item)
        items.remove(item)
        notifyItemRemoved(idx)
        notifyDataSetChanged()
    }

    fun decrement(item:InventoryItem){
        item.amount--
        if(abs(item.amount* 1000 /1000.toDouble()-item.amount)<0.0001)
            item.amount=item.amount* 1000 /1000.toDouble()
        notifyDataSetChanged()
    }
    fun increment(item: InventoryItem){
        item.amount++
        if(abs(item.amount* 1000 /1000.toDouble()-item.amount)<0.0001)
            item.amount=item.amount* 1000 /1000.toDouble()
        notifyDataSetChanged()
    }

    inner class InventoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nameTextView: TextView
        val amountTextView: TextView
        val categoryTextView: TextView
        val expiryTextView:TextView
        val decrementButton:ImageButton
        val incrementButton:ImageButton
        val removeButton: ImageButton

        var item: InventoryItem? = null

        init {
            nameTextView = itemView.findViewById(R.id.InventoryItemNameTextView)
            amountTextView = itemView.findViewById(R.id.InventoryItemAmountTextView)
            categoryTextView = itemView.findViewById(R.id.InventoryItemCategoryTextView)
            expiryTextView=itemView.findViewById(R.id.InventoryItemExpiryTextView)
            decrementButton=itemView.findViewById(R.id.InventoryItemDecrementButton)
            incrementButton=itemView.findViewById(R.id.InventoryItemIncrementButton)
            removeButton = itemView.findViewById(R.id.InventoryItemRemoveButton)
            itemView.setOnClickListener{
                listener.onItemSelected(item)
            }
            decrementButton.setOnClickListener{
                item?.let { it1 -> decrement(it1) }
                item?.let { it1 -> listener.onItemChanged(it1) }
            }
            incrementButton.setOnClickListener{
                item?.let { it1 -> increment(it1) }
                item?.let { it1 -> listener.onItemChanged(it1) }
            }
            removeButton.setOnClickListener{
                deleteItem(item)
                item?.let { it1 -> listener.onItemDeleted(it1) }
            }

        }
    }
}