package com.example.inventur

import android.content.Context
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKManager.EMDKListener;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.ScanDataCollection.ScanData;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.Scanner.DataListener;
import com.symbol.emdk.barcode.Scanner.StatusListener;
import com.symbol.emdk.barcode.Scanner.TriggerType;
import com.symbol.emdk.barcode.ScannerConfig;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;
import com.symbol.emdk.barcode.StatusData.ScannerStates;

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.inventur.enums.Overviews
import com.example.inventur.fragments.DeviceFragment
import com.example.inventur.fragments.InventoryFragment
import com.example.inventur.fragments.OverviewFragment
import com.example.inventur.toolbars.EntryToolbar
import com.example.inventur.toolbars.OverviewToolbar
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class MainActivity : AppCompatActivity() , OverviewFragment.OnAddClickListener, EntryToolbar.OnSaveClickListener, OverviewToolbar.OnTrashClickedListener,
    OverviewFragment.OnRecyclerItemClickListener, EntryToolbar.OnBackClickListener, OverviewToolbar.OnBackClickListener, EMDKListener, StatusListener, DataListener{

    //UI related variables
    private val manager: FragmentManager = supportFragmentManager
    private var overview: Overviews = Overviews.INVENTORIES
    private var inventory: String = ""

    //Variables to hold EMDK related objects
    private var emdkManager: EMDKManager? = null
    private var barcodeManager: BarcodeManager? = null
    private var scanner: Scanner? = null


    //##########################################################
    //################### Activity functions ###################
    //##########################################################

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Requests the EMDKManager object
        val results = EMDKManager.getEMDKManager(applicationContext, this)

        //Check the return status of getEMDKManager()
        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            Log.i("SCANNER", "EDMKManager object request failed!")
        } else {
            Log.i("SCANNER", "EDMKManager object initialization in progress...")
        }

        //UI related calls
        setContentView(R.layout.activity_main)
        showOverviewFragment()
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        // set onClickListeners dependent on active fragments
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

    override fun onResume() {
        super.onResume()
        if(emdkManager != null) {
            initBarcodeManager()
            initScanner()
        }
    }

    override fun onPause() {
        super.onPause()
        deInitScanner()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release all the EMDK resources
        if (emdkManager != null) {
            emdkManager!!.release()
            emdkManager = null
        }
    }

    //###############################################################
    //################### EMDK Interface functions ##################
    //###############################################################

    //is triggered when EMDK is ready to be used
    override fun onOpened(emdkManager: EMDKManager) {
        this.emdkManager = emdkManager
        initBarcodeManager()
        initScanner()
    }

    override fun onClosed() {
        // The EMDK closed unexpectedly. Release all the resources
        if (emdkManager != null) {
            emdkManager!!.release()
            emdkManager = null
        }
    }

    override fun onStatus(statusData: StatusData) {
        when (statusData.state) {
            ScannerStates.IDLE -> {
                Log.i("SCANNER", "Scanner is enabled and idle...")
                scanner!!.config
                try {
                    scanner!!.read()
                } catch (e: Exception) {
                    Log.i("SCANNER", e.message)
                }
            }
            ScannerStates.WAITING -> Log.i("SCANNER", "Waiting for scan...")
            ScannerStates.SCANNING -> Log.i("SCANNER", "Scanning...")
            ScannerStates.DISABLED -> Log.i("SCANNER", "Scanner is disabled")
            ScannerStates.ERROR -> Log.i("SCANNER", "An error has occurred.")
            else -> {}
        }
    }

    // callback method when data is received through scanning
    override fun onData(scanDataCollection: ScanDataCollection?) {
        // The ScanDataCollection object gives scanning result and the collection of ScanData. Check the data and its status
        var dataStr = ""
        if ((scanDataCollection != null) && (scanDataCollection.result == ScannerResults.SUCCESS)) {
            val scanData = scanDataCollection.scanData
            for (data in scanData) {
                // Get the scanned dataString barcodeData = data.data
                val barcodeData = data.data
                // Get the type of label being scanned
                val labelType = data.labelType
                dataStr = barcodeData
            }
            // result of scanned data
            Log.i("SCANNER", "Scanned barcode $dataStr")
            addNewDevice(dataStr)
        }
    }

    //########################################################
    //################### Scanner functions ##################
    //########################################################

    private fun initBarcodeManager() {
        barcodeManager = emdkManager!!.getInstance(EMDKManager.FEATURE_TYPE.BARCODE) as BarcodeManager
        if (barcodeManager == null) {
            Log.i("SCANNER", "Barcode scanning is not supported.")
        }
    }

    private fun initScanner () {
        when (scanner) {
            null -> {
                scanner = barcodeManager!!.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT)
                when (scanner) {
                    null ->  Log.i("SCANNER", "Failed to initialize the scanner device")
                    else -> {
                        scanner!!.addDataListener(this)
                        scanner!!.addStatusListener(this)
                        scanner!!.triggerType = TriggerType.HARD
                        try {
                            scanner!!.enable()
                            Log.i("SCANNER", "Initialization of scanner successful!")
                        } catch (e: ScannerException) {
                            Log.i("SCANNER", e.message)
                            deInitScanner()
                        }
                    }
                }
            }
        }
    }

    private fun deInitScanner() {
        if (scanner != null) {
            try {
                scanner!!.release()
            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                Log.i("SCANNER", e.message)
            }
            scanner = null
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
        Log.i("CALLBACK", "addButton callback received on $overview")
    }

    override fun onSaveButtonClicked() {
        Log.i("CALLBACK", "saveButton callback received on $overview")
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

        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val fileContent = sharedPref.getString(Overviews.DEVICES.toString(), "")
        var myExternalFile: File = File(getExternalFilesDir(""),"Inventur.txt")
        try {
            val fileOutPutStream = FileOutputStream(myExternalFile)
            fileOutPutStream.write(fileContent!!.toByteArray())
            fileOutPutStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
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
        Log.i("CALLBACK", "backPressed callback received on ${manager.findFragmentById(R.id.fragment_holder)!!.tag}")
    }

    override fun onTrashButtonClicked() {
        val fragment = manager.findFragmentById(R.id.fragment_holder)
        (fragment as OverviewFragment).switchDeleteMode()
        Log.i("CALLBACK", "trashButton callback received on $overview")
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
        Log.i("CALLBACK", "recyclerItem callback received \n data: $jsonObject \n position: $position")
    }

    override fun onOverviewBackButtonClicked() {
        overview = Overviews.INVENTORIES
        showOverviewFragment()
        Log.i("CALLBACK", "overviewBackButton callback received on $overview")
    }

    override fun onEntryBackButtonClicked() {
        showOverviewFragment()
        Log.i("CALLBACK", "entryBackButton callback received on $overview")
    }

    //############################################################
    //################### showFragmentFunctions ##################
    //############################################################

    private fun showOverviewFragment() {
        val transaction = manager.beginTransaction()
        val fragment = OverviewFragment.newInstance(overview, inventory)
        transaction.replace(R.id.fragment_holder, fragment, "OVERVIEW_FRAGMENT")
        transaction.commit()
        Log.i("FRAGMENT", "OverviewFragment now active in fragment_holder")
        showOverviewToolbar()
    }

    private fun showDeviceFragment(jsonObject: JSONObject = JSONObject(), editMode: Boolean = false, position: Int = 0) {
        val transaction = manager.beginTransaction()
        val fragment = DeviceFragment.newInstance(jsonObject, editMode, position, inventory)
        transaction.replace(R.id.fragment_holder, fragment, "DEVICE_FRAGMENT")
        transaction.commit()
        Log.i("FRAGMENT", "DeviceFragment now active in fragment_holder")
        showEntryToolbar()
    }

    private fun showInventoryFragment() {
        val transaction = manager.beginTransaction()
        val fragment = InventoryFragment.newInstance()
        transaction.replace(R.id.fragment_holder, fragment, "INVENTORY_FRAGMENT")
        transaction.commit()
        Log.i("FRAGMENT", "InventoryFragment now active in fragment_holder")
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
        Log.i("FRAGMENT", "OverviewToolbar now active in toolbar_holder")
    }

    private fun showEntryToolbar() {
        val transaction = manager.beginTransaction()
        val toolbar = EntryToolbar.newInstance()
        transaction.replace(R.id.toolbar_holder, toolbar, "ENTRY_TOOLBAR")
        transaction.commit()
        Log.i("FRAGMENT", "EntryToolbar now active in toolbar_holder")
    }

    //############################################################
    //################### internal functions #####################
    //############################################################
    fun addNewDevice(deviceNumber: String) {
        if (overview == Overviews.DEVICES && manager.findFragmentById(R.id.fragment_holder)!!.tag == "OVERVIEW_FRAGMENT") {
            val jsonString = "{ \"${resources.getString(R.string.device_number)}\":\"$deviceNumber\"}"
            val jsonObject = JSONObject(jsonString)
            showDeviceFragment(jsonObject)
        }
    }
}
