package com.example.inventur.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.inventur.R
import com.example.inventur.enums.Overviews
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class InventoryFragment : Fragment() {

    private lateinit var inventoryName : EditText

    companion object {
        fun newInstance() : InventoryFragment{
            return InventoryFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_inventory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inventoryName = view.findViewById(R.id.inventory_name)
    }

    fun saveAsJSON() {
        val sharedPref = activity!!.getPreferences(Context.MODE_PRIVATE) ?: return
        var jsonString = sharedPref.getString(Overviews.INVENTORIES.toString(), "")

        //check if there is already an object in sharedPref
        val inventoryData = when {
            jsonString.isNullOrEmpty() -> JSONArray()
            else -> JSONArray(jsonString)
        }
        inventoryData.put(JSONObject(toJSONString()))

        jsonString = sharedPref.getString(Overviews.DEVICES.toString(), "")
        val deviceData = JSONObject(jsonString).put(inventoryName.text.toString(), JSONArray())

        with (sharedPref.edit()) {
            putString(Overviews.INVENTORIES.toString(), inventoryData.toString())
            putString(Overviews.DEVICES.toString(), deviceData.toString())
            commit()
        }
    }

    private fun toJSONString() : String {
        return "{" + "\"" + inventoryName.hint + "\":\"" + inventoryName.text + "\"" + "}"
    }
}