package hu.bme.aut.inventoryapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.inventoryapp.R
import hu.bme.aut.shoppinglist.data.ShoppingListItem

class ShoppingListAdapter(private val listener: ShoppingListItemClickListener):
    RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>() {
    private val items = mutableListOf<ShoppingListItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListAdapter.ShoppingListViewHolder {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_shopping_list, parent, false)
        return ShoppingListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ShoppingListAdapter.ShoppingListViewHolder, position: Int) {
        val item = items[position]
        holder.nameTextView.text = item.name
        holder.isBoughtCheckBox.isChecked=item.isBought
        holder.item = item
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface ShoppingListItemClickListener{
        fun onItemRemoved(item: ShoppingListItem)
        fun onItemChanged(item: ShoppingListItem)
    }

    fun addItem(item: ShoppingListItem){
        items.add(item)
        notifyItemInserted(items.size-1)
    }

    fun update(shoppingListItems: List<ShoppingListItem>){
        items.clear()
        items.addAll(shoppingListItems)
        notifyDataSetChanged()
    }

    fun deleteItem(item:ShoppingListItem){
        val idx= items.indexOf(element = item)
        items.remove(item)
        notifyItemRemoved(idx)
    }

    fun deleteAll() {
        for(item in items)
            listener.onItemRemoved(item)
        items.clear()
        notifyDataSetChanged()
    }

    inner class ShoppingListViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val nameTextView:TextView
        val isBoughtCheckBox:CheckBox
        val removeButton:ImageButton

        var item:ShoppingListItem?=null

        init{
            nameTextView=itemView.findViewById(R.id.ShoppingListItemNameTextView)
            isBoughtCheckBox=itemView.findViewById(R.id.ShoppingListItemIsPurchasedCheckBox)
            removeButton=itemView.findViewById(R.id.ShoppingListItemRemoveButton)

            removeButton.setOnClickListener{
                item?.let { it1 -> deleteItem(it1) }
                item?.let { it1 -> listener.onItemRemoved(it1) }
            }
            isBoughtCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
                item?.let{
                    val newItem = it.copy(
                        isBought = isChecked
                    )
                    item=newItem
                    listener.onItemChanged(newItem)
                }
            }

        }
    }



}