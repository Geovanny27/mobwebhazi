package hu.bme.aut.inventoryapp


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.NavUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import hu.bme.aut.inventoryapp.adapter.ShoppingListAdapter
import hu.bme.aut.inventoryapp.data.ShoppingListDatabase
import hu.bme.aut.inventoryapp.fragments.NewShoppingListItemDialogFragment
import hu.bme.aut.shoppinglist.data.ShoppingListItem
import kotlinx.android.synthetic.main.activity_shopping_list.*
import kotlinx.android.synthetic.main.content_main.*
import kotlin.concurrent.thread

class ShoppingListActivity : AppCompatActivity(),ShoppingListAdapter.ShoppingListItemClickListener,NewShoppingListItemDialogFragment.NewShoppingListItemDialogListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ShoppingListAdapter
    private lateinit var database: ShoppingListDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)
        fab.setOnClickListener{
            NewShoppingListItemDialogFragment().show(
                supportFragmentManager,
                NewShoppingListItemDialogFragment.TAG
            )
        }
        database = Room.databaseBuilder(
            applicationContext,
            ShoppingListDatabase::class.java,
            "shopping-list"
        ).build()
        initRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_shopping_list, menu)
        return true
    }

    private fun initRecyclerView() {
        recyclerView = MainRecyclerView
        adapter = ShoppingListAdapter(this)
        loadItemsInBackground()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadItemsInBackground() {
        thread {
            val items = database.shoppingListItemDao().getAll()
            runOnUiThread {
                adapter.update(items)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                true
            }
            R.id.action_delete_all->{
                adapter.deleteAll()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onItemRemoved(item: ShoppingListItem) {
        thread{
            database.shoppingListItemDao().deleteItem(item)
        }
    }

    override fun onItemChanged(item: ShoppingListItem) {
        thread{
            database.shoppingListItemDao().update(item)
        }
    }


    override fun onShoppingListItemCreated(newItem: ShoppingListItem) {
        thread {
            val newId = database.shoppingListItemDao().insert(newItem)
            val newShoppingItem = newItem.copy(
                id = newId
            )
            runOnUiThread {
                adapter.addItem(newShoppingItem)
            }
        }
    }





}