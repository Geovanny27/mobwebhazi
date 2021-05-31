package hu.bme.aut.inventoryapp

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import hu.bme.aut.inventoryapp.adapter.InventoryAdapter
import hu.bme.aut.inventoryapp.data.InventoryDatabase
import hu.bme.aut.inventoryapp.data.InventoryItem
import hu.bme.aut.inventoryapp.fragments.NewInventoryCategoryDialogFragment
import hu.bme.aut.inventoryapp.fragments.NewInventoryItemDialogFragment
import hu.bme.aut.inventoryapp.fragments.NewItemAmountDialogFragment
import hu.bme.aut.inventoryapp.notification.NotificationUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*
import kotlin.collections.HashSet
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity(), InventoryAdapter.InventoryItemClickListener,
    NewInventoryItemDialogFragment.NewInventoryItemDialogListener, NewInventoryCategoryDialogFragment.NewInventoryCategoryDialogListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InventoryAdapter
    private lateinit var database: InventoryDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        fab.setOnClickListener {
            if (View.GONE == fabBGLayout.visibility) {
                showFABMenu()
            } else {
                closeFABMenu()
            }
        }
        fab1.setOnClickListener{
            NewInventoryItemDialogFragment().show(
                supportFragmentManager,
                NewInventoryItemDialogFragment.TAG
            )
        }
        fab2.setOnClickListener{
            NewInventoryCategoryDialogFragment().show(
                supportFragmentManager,
                NewInventoryCategoryDialogFragment.TAG
            )
        }
        val sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this)
        if(sharedPreferences.getBoolean("dark_mode",false))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        fabBGLayout.setOnClickListener { closeFABMenu() }

        database = Room.databaseBuilder(
            applicationContext,
            InventoryDatabase::class.java,
            "inventory"
        ).build()

        initRecyclerView()
    }

    private fun showFABMenu() {
        fabLayout1.visibility = View.VISIBLE
        fabLayout2.visibility = View.VISIBLE
        fabBGLayout.visibility = View.VISIBLE
        fab.animate().rotationBy(180F)
        fabLayout1.animate().translationY(-resources.getDimension(R.dimen.standard_75))
        fabLayout2.animate().translationY(-resources.getDimension(R.dimen.standard_120))
    }

    private fun closeFABMenu() {
        fabBGLayout.visibility = View.GONE
        fab.animate().rotation(0F)
        fabLayout1.animate().translationY(0f)
        fabLayout2.animate().translationY(0f)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}
                override fun onAnimationEnd(animator: Animator) {
                    if (View.GONE == fabBGLayout.visibility) {
                        fabLayout1.visibility = View.GONE
                        fabLayout2.visibility = View.GONE
                    }
                }

                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
            })

    }

    private fun initRecyclerView() {
        recyclerView = MainRecyclerView
        adapter = InventoryAdapter(this)
        loadItemsInBackground()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadItemsInBackground() {
        thread {
            val items = database.inventoryItemDao().getAll()
            runOnUiThread {
                adapter.update(items)
            }
        }
    }

    override fun onItemChanged(item: InventoryItem) {
        thread {
            database.inventoryItemDao().update(item)
            Log.d("MainActivity", "InventoryItem update was successful")
        }
        adapter.notifyDataSetChanged()
    }

    override fun onItemDeleted(item: InventoryItem) {
        thread {
            database.inventoryItemDao().deleteItem(item)
        }
    }

    override fun onItemSelected(item: InventoryItem?) {
        if(item != null) {
            NewItemAmountDialogFragment(item).show(
                supportFragmentManager,
                NewInventoryItemDialogFragment.TAG
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this,SettingsActivity::class.java))
                true
            }
            R.id.action_shopping_list->{
                startActivity(Intent(this,ShoppingListActivity::class.java))
                true
            }
            R.id.action_try_notification->{
                NotificationUtils().setNotification(Calendar.getInstance().timeInMillis+1000, this@MainActivity)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onInventoryItemCreated(newItem: InventoryItem) {
        thread {
            val newId = database.inventoryItemDao().insert(newItem)
            val newInventoryItem = newItem.copy(
                id = newId
            )
            runOnUiThread {
                adapter.addItem(newInventoryItem)
            }
        }
        if(newItem.expiry.isNotEmpty()){
            var time=getNotificationTime(newItem)
            time.add(Calendar.DATE,-1)
            NotificationUtils().setNotification(time.timeInMillis-Calendar.getInstance().timeInMillis, this@MainActivity)
        }
    }

    override fun onInventoryCategoryCreated(newCategory: String) {
        val sharedPreferences = getSharedPreferences("SP_CATEGORIES",Context.MODE_PRIVATE)
        val categories = HashSet(sharedPreferences.getStringSet("CATEGORIES", mutableSetOf<String>()))
        categories.add(newCategory)
        val editor=sharedPreferences.edit()
        editor.putStringSet("CATEGORIES",categories)
        editor.apply()
    }

    private fun getNotificationTime(item:InventoryItem):Calendar{
        val datePieces=item.expiry.split("/")
        var date:Calendar = Calendar.getInstance()
        date.set(Calendar.YEAR,datePieces[2].toInt())
        date.set(Calendar.MONTH,datePieces[1].toInt()-1)
        date.set(Calendar.DATE,datePieces[0].toInt())
        return date
    }
}