package hu.bme.aut.inventoryapp.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import hu.bme.aut.inventoryapp.R
import hu.bme.aut.shoppinglist.data.ShoppingListItem
import java.lang.RuntimeException

class NewShoppingListItemDialogFragment : DialogFragment() {
    private lateinit var nameEditText:EditText

    interface NewShoppingListItemDialogListener{
        fun onShoppingListItemCreated(newItem: ShoppingListItem)
    }

    private lateinit var listener: NewShoppingListItemDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewShoppingListItemDialogListener ?: throw RuntimeException("Activity must implement the NewShoppingListItemDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.new_shopping_list_item)
            .setView(getContentView())
            .setPositiveButton(R.string.ok) { _, _ ->
                if (isValid()) {
                    listener.onShoppingListItemCreated(getShoppingListItem())
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    private fun getContentView(): View {
        val contentView =
            LayoutInflater.from(context).inflate(R.layout.dialog_new_shopping_item, null)
        nameEditText = contentView.findViewById(R.id.ShoppingListItemNameEditText)
        return contentView
    }

    private fun isValid()=nameEditText.text.isNotEmpty()

    private fun getShoppingListItem() = ShoppingListItem(
        id=null,
        name = nameEditText.text.toString(),
        isBought = false
    )

    companion object {
        const val TAG = "NewShoppingListItemDialogFragment"
    }
}