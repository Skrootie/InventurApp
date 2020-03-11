package com.example.inventur

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.inventur.enums.Overviews
import com.example.inventur.fragments.DeviceFragment
import com.example.inventur.fragments.InventoryFragment
import com.example.inventur.fragments.OverviewFragment
import com.example.inventur.toolbars.EntryToolbar
import com.example.inventur.toolbars.OverviewToolbar
import org.json.JSONObject

class MainActivity : AppCompatActivity() , OverviewFragment.OnAddClickListener, EntryToolbar.OnSaveClickListener, OverviewToolbar.OnTrashClickedListener,
    OverviewFragment.OnRecyclerItemClickListener, EntryToolbar.OnBackClickListener, OverviewToolbar.OnBackClickListener{

    private val manager: FragmentManager = supportFragmentManager
    private var overview: Overviews = Overviews.INVENTORIES
    private var inventory: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showOverviewFragment()
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        when (fragment) {
            is OverviewFragment -> {fragment.setOnAddClickListener(this)
                fragment.setOnItemClickListener(this)}
            is OverviewToolbar -> {
                fragment.setOnTrashButtonClicked(this)
                fragment.setOnBackButtonClicked(this)
            }
            is EntryToolbar -> {fragment.setOnBackClickListener(this)
                fragment.setOnSaveClickListener(this)}
        }
    }

    //#######################################################
    //################### OnClickListeners ##################
    //#######################################################

    override fun onAddButtonClicked() {
        when (overview) {
            Overviews.DEVICES -> showDeviceFragment()
            Overviews.INVENTORIES -> showInventoryFragment()
        }
    }

    override fun onSaveButtonClicked() {
        val fragment = manager.findFragmentById((R.id.fragment_holder))
        when (overview) {
            Overviews.DEVICES -> {
                (fragment as DeviceFragment).saveAsJSON()
                showOverviewFragment()
            }
            Overviews.INVENTORIES -> {
                (fragment as InventoryFragment).saveAsJSON()
                showOverviewFragment()
            }
        }

    }

    override fun onBackPressed() {
        when (manager.findFragmentById(R.id.fragment_holder)!!.tag) {
            "DEVICE_FRAGMENT" -> showOverviewFragment()
            "OVERVIEW_FRAGMENT" -> {
                when (overview) {
                    Overviews.INVENTORIES -> super.onBackPressed()
                    Overviews.DEVICES -> {
                        overview = Overviews.INVENTORIES
                        showOverviewFragment()
                    }
                }
            }
            "INVENTORY_FRAGMENT" -> showOverviewFragment()
        }
    }

    override fun onTrashButtonClicked() {
        val fragment = manager.findFragmentById(R.id.fragment_holder)
        (fragment as OverviewFragment).switchDeleteMode()
    }

    override fun onRecyclerItemClicked(jsonObject: JSONObject, position: Int) {
        when (overview) {
            Overviews.INVENTORIES -> {
                overview = Overviews.DEVICES
                inventory = jsonObject.getString("Inventurname")
                showOverviewFragment()
            }
            Overviews.DEVICES -> {
                showDeviceFragment(jsonObject, true, position)
            }
        }
    }

    override fun onOverviewBackButtonClicked() {
        overview = Overviews.INVENTORIES
        showOverviewFragment()
    }

    override fun onBackButtonClicked() {
        showOverviewFragment()
    }

    //############################################################
    //################### showFragmentFunctions ##################
    //############################################################

    private fun showOverviewFragment() {
        val transaction = manager.beginTransaction()
        val fragment = OverviewFragment.newInstance(overview, inventory)
        transaction.replace(R.id.fragment_holder, fragment, "OVERVIEW_FRAGMENT")
        transaction.commit()
        showOverviewToolbar()
    }

    private fun showDeviceFragment(jsonObject: JSONObject = JSONObject(), editMode: Boolean = false, position: Int = 0) {
        val transaction = manager.beginTransaction()
        val fragment = DeviceFragment.newInstance(jsonObject, editMode, position, inventory)
        transaction.replace(R.id.fragment_holder, fragment, "DEVICE_FRAGMENT")
        transaction.commit()
        showEntryToolbar()
    }

    private fun showInventoryFragment() {
        val transaction = manager.beginTransaction()
        val fragment = InventoryFragment.newInstance()
        transaction.replace(R.id.fragment_holder, fragment, "INVENTORY_FRAGMENT")
        transaction.commit()
        showEntryToolbar()
    }

    private fun showOverviewToolbar() {
        val transaction = manager.beginTransaction()
        val toolbar = when (overview) {
            Overviews.DEVICES -> OverviewToolbar.newInstance(true, inventory)
            Overviews.INVENTORIES -> OverviewToolbar.newInstance(false, "Inventuren")
        }
        transaction.replace(R.id.toolbar_holder, toolbar, "OVERVIEW_TOOLBAR")
        transaction.commit()
    }

    private fun showEntryToolbar() {
        val transaction = manager.beginTransaction()
        val toolbar = EntryToolbar.newInstance()
        transaction.replace(R.id.toolbar_holder, toolbar, "ENTRY_TOOLBAR")
        transaction.commit()
    }
}
