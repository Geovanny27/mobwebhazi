package hu.bme.aut.inventoryapp.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.DialogFragment
import com.google.android.material.internal.ViewUtils.getContentView
import hu.bme.aut.inventoryapp.R
import hu.bme.aut.inventoryapp.adapter.InventoryAdapter
import hu.bme.aut.inventoryapp.data.InventoryItem

class NewItemAmountDialogFragment(private val item:InventoryItem): DialogFragment() {
    private lateinit var amountEditText:EditText
    private lateinit var listener: InventoryAdapter.InventoryItemClickListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener= context as? InventoryAdapter.InventoryItemClickListener
            ?:throw RuntimeException("Activity must implement the InventoryItemClickListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.new_item_amount)
            .setView(getContentView())
            .setPositiveButton(R.string.ok) { _, _ ->
                if (isValid()) {
                    item.amount=amountEditText.text.toString().toDouble()
                    listener.onItemChanged(item)
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    private fun isValid()=amountEditText.text.isNotEmpty()

    private fun getContentView(): View {
        val contentView=LayoutInflater.from(context).inflate(R.layout.dialog_new_item_amount,null)
        amountEditText=contentView.findViewById(R.id.ItemAmountEditText)
        return contentView
    }
}