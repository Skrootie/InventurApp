package com.example.inventur

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.inventur.fragments.DeviceFragment
import com.example.inventur.fragments.OverviewFragment
import com.example.inventur.toolbars.DeviceToolbar
import com.example.inventur.toolbars.OverviewToolbar
import org.json.JSONObject

class MainActivity : AppCompatActivity() , OverviewFragment.OnAddClickListener, DeviceFragment.OnClickListener, OverviewToolbar.OnTrashClickedListener,
    OverviewFragment.OnRecyclerItemClickListener, DeviceToolbar.OnBackClickListener{

    private val manager: FragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showOverviewFragment()
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        when (fragment) {
            is OverviewFragment -> {fragment.setOnAddClickListener(this); fragment.setOnItemClickListener(this)}
            is DeviceFragment -> fragment.setOnClickListener(this)
            is OverviewToolbar -> fragment.setOnTrashButtonClicked(this)
            is DeviceToolbar -> fragment.setOnBackClickListener(this)
        }
    }

    //#######################################################
    //################### OnClickListeners ##################
    //#######################################################

    override fun onAddButtonClicked() {
        showEmptyDeviceFragment()
    }

    override fun onSaveButtonClicked() {
        val fragment = manager.findFragmentById(R.id.fragment_holder)
        (fragment as DeviceFragment).saveAsString()
        showOverviewFragment()
    }

    override fun onBackPressed() {
        when (manager.findFragmentById(R.id.fragment_holder)!!.tag) {
            "DEVICE_FRAGMENT" -> showOverviewFragment()
            "OVERVIEW_FRAGMENT" -> super.onBackPressed()
        }
    }

    override fun onTrashButtonClicked() {
        val fragment = manager.findFragmentById(R.id.fragment_holder)
        (fragment as OverviewFragment).switchDeleteMode()
    }

    override fun onRecyclerItemClicked(jsonObject: JSONObject, position: Int) {
        showFilledDeviceFragment(jsonObject, position)
        //val fragment = manager.findFragmentById(R.id.fragment_holder)
        //(fragment as DeviceFragment).loadEntry(jsonObject)
    }

    override fun onBackButtonClicked() {
        showOverviewFragment()
    }

    //############################################################
    //################### showFragmentFunctions ##################
    //############################################################

    private fun showOverviewFragment() {
        val transaction = manager.beginTransaction()
        val fragment = OverviewFragment.newInstance()
        transaction.replace(R.id.fragment_holder, fragment, "OVERVIEW_FRAGMENT")
        transaction.commit()
        showOverviewToolbar()
    }

    private fun showEmptyDeviceFragment() {
        val transaction = manager.beginTransaction()
        val fragment = DeviceFragment.newInstance()
        transaction.replace(R.id.fragment_holder, fragment, "DEVICE_FRAGMENT")
        transaction.commit()
        showDeviceToolbar()
    }

    private fun showFilledDeviceFragment(jsonObject: JSONObject, position: Int) {
        val transaction = manager.beginTransaction()
        val fragment = DeviceFragment.newInstance(jsonObject, true, position)
        transaction.replace(R.id.fragment_holder, fragment, "DEVICE_FRAGMENT")
        transaction.commit()
        showDeviceToolbar()
    }

    private fun showOverviewToolbar() {
        val transaction = manager.beginTransaction()
        val toolbar = OverviewToolbar.newInstance()
        transaction.replace(R.id.toolbar_holder, toolbar, "OVERVIEW_TOOLBAR")
        transaction.commit()
    }

    private fun showDeviceToolbar() {
        val transaction = manager.beginTransaction()
        val toolbar = DeviceToolbar.newInstance()
        transaction.replace(R.id.toolbar_holder, toolbar, "DEVICE_TOOLBAR")
        transaction.commit()
    }
}
