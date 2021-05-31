package hu.bme.aut.inventoryapp.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.inventoryapp.R
import hu.bme.aut.inventoryapp.data.InventoryItem
import java.util.*


class NewInventoryItemDialogFragment : DialogFragment() {
    private lateinit var nameEditText: EditText
    private lateinit var amountEditText: EditText
    private lateinit var unitSpinner:Spinner
    private lateinit var categorySpinner: Spinner
    private lateinit var categoriesList:List<String>
    private lateinit var expiryEditText: EditText

    interface NewInventoryItemDialogListener {
        fun onInventoryItemCreated(newItem: InventoryItem)
    }

    private lateinit var listener: NewInventoryItemDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewInventoryItemDialogListener
            ?: throw RuntimeException("Activity must implement the NewInventoryItemDialogListener interface!")
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.new_inventory_item)
            .setView(getContentView())
            .setPositiveButton(R.string.ok) { _, _ ->
                if (isValid()) {
                    listener.onInventoryItemCreated(getInventoryItem())
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    companion object {
        const val TAG = "NewInventoryItemDialogFragment"
    }

    private fun getContentView(): View {
        val contentView =
            LayoutInflater.from(context).inflate(R.layout.dialog_new_inventory_item, null)
        nameEditText = contentView.findViewById(R.id.InventoryItemNameEditText)
        amountEditText = contentView.findViewById(R.id.InventoryItemAmountEditText)
        unitSpinner=contentView.findViewById(R.id.InventoryItemUnitSpinner)
        expiryEditText=contentView.findViewById(R.id.InventoryItemExpiryEditText)
        unitSpinner.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.units)
            )
        )
        categorySpinner = contentView.findViewById(R.id.InventoryItemCategorySpinner)
        val sharedPreferences= context?.getSharedPreferences("SP_CATEGORIES",Context.MODE_PRIVATE)
        val categories= sharedPreferences?.getStringSet("CATEGORIES",null)
        if(categories!=null){
            categoriesList = categories.toList()
            categorySpinner.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    categoriesList
                )
            )
        }
        else{
            categoriesList = listOf("")
            categorySpinner.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    categoriesList
                )
            )
        }
        expiryEditText.addTextChangedListener(object : TextWatcher {
            private var current = ""
            private val ddmmyyyy = "DDMMYYYY"
            private val cal: Calendar = Calendar.getInstance()
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (s.toString() != current) {
                    var clean = s.toString().replace("[^\\d.]".toRegex(), "")
                    val cleanC = current.replace("[^\\d.]".toRegex(), "")
                    val cl = clean.length
                    var sel = cl
                    var i = 2
                    while (i <= cl && i < 6) {
                        sel++
                        i += 2
                    }
                    if (clean == cleanC) sel--
                    if (clean.length < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length)
                    } else {
                        var day = clean.substring(0, 2).toInt()
                        var mon = clean.substring(2, 4).toInt()
                        var year = clean.substring(4, 8).toInt()
                        if (mon > 12) mon = 12
                        cal.set(Calendar.MONTH, mon - 1)
                        year = if (year < 1900) 1900 else if (year > 2100) 2100 else year
                        cal.set(Calendar.YEAR, year)
                        day = if (day > cal.getActualMaximum(Calendar.DATE)) cal.getActualMaximum(
                            Calendar.DATE
                        ) else day
                        clean = String.format("%02d%02d%02d", day, mon, year)
                    }
                    clean = String.format(
                        "%s/%s/%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8)
                    )
                    sel = if (sel < 0) 0 else sel
                    current = clean
                    expiryEditText.setText(current)
                    expiryEditText.setSelection(if (sel < current.length) sel else current.length)
                }
            }

            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {}
        })

        return contentView
    }

    private fun isValid() = nameEditText.text.isNotEmpty()&&amountEditText.text.isNotEmpty()


    private fun getInventoryItem() = InventoryItem(
        id = null,
        name = nameEditText.text.toString(),
        amount = amountEditText.text.toString().toDouble(),
        unit = resources.getStringArray(R.array.units)[unitSpinner.selectedItemPosition],
        category = categoriesList[categorySpinner.selectedItemPosition],
        expiry = expiryEditText.text.toString()
    )


}