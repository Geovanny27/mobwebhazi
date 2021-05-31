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

class NewInventoryCategoryDialogFragment:DialogFragment() {
    private lateinit var nameEditText:EditText

    interface NewInventoryCategoryDialogListener{
        fun onInventoryCategoryCreated(newCategory:String)
    }

    private lateinit var listener: NewInventoryCategoryDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewInventoryCategoryDialogListener
            ?: throw RuntimeException("Activity must implement the NewInventoryItemDialogListener interface!")
    }

    companion object {
        const val TAG = "NewInventoryCategoryDialogFragment"
    }

    private fun getContentView(): View {
        val contentView= LayoutInflater.from(context).inflate(R.layout.dialog_new_inventory_category,null)
       nameEditText=contentView.findViewById(R.id.InventoryCategoryNameEditText)
        return contentView
    }

    private fun isValid()=nameEditText.text.isNotEmpty()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.new_inventory_category)
            .setView(getContentView())
            .setPositiveButton(R.string.ok) { _, _ ->
                if (isValid()) {
                    listener.onInventoryCategoryCreated(nameEditText.text.toString())
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }
}