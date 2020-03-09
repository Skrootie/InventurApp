package com.example.inventur.fragments

import android.content.Context
import android.media.Image
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.set
import androidx.fragment.app.Fragment
import com.example.inventur.R
import kotlinx.android.synthetic.main.fragment_device.view.*
import org.json.JSONArray
import org.json.JSONObject

class DeviceFragment : Fragment() {

    lateinit var option : Spinner
    lateinit var ctx : Context
    internal lateinit var callback: OnClickListener
    lateinit var saveButton : ImageButton
    private lateinit var viewGroup: ViewGroup
    private lateinit var jsonObject: JSONObject
    private lateinit var views : MutableList<View>
    private var position: Int = 0
    private var editMode = false

    fun setOnClickListener(callback: OnClickListener) {
        this.callback = callback
    }

    interface OnClickListener {
        fun onSaveButtonClicked()
    }

    companion object {
        fun newInstance(jsonObject: JSONObject = JSONObject(), editMode: Boolean = false, position: Int = 0) : DeviceFragment {
            val fragment = DeviceFragment()
            fragment.jsonObject = jsonObject
            fragment.editMode = editMode
            fragment.position = position
            return fragment
        }
    }

    //#######################################################
    //################### View functions ####################
    //#######################################################

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewGroup = container!!
        ctx = activity!!.applicationContext

        return inflater.inflate(R.layout.fragment_device, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*option = view.findViewById(R.id.spinner)
        val options = arrayOf("Wandhalterung (Arm)", "Gestellhalterung (Arm)", "Gestellhalterung")
        option.adapter = ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, options)*/

        saveButton = view.findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            callback.onSaveButtonClicked()
        }
        val count = (view as ConstraintLayout).childCount
        views = mutableListOf<View>()
        for(i in 0 until count) {
            when (val v =view.getChildAt(i)) {
                is EditText -> {views.add(v); if (jsonObject.length() > 0) v.setText(jsonObject[v.hint.toString()].toString(), TextView.BufferType.EDITABLE)}
            }
        }
    }

    //#########################################################
    //################### internal functions ##################
    //#########################################################

    fun saveAsString() {
        val sharedPref = activity!!.getPreferences(Context.MODE_PRIVATE) ?: return
        val defaultValue = ""
        var oldJsonString = sharedPref.getString("geier", defaultValue)
        var jsonArray : JSONArray

        //check if there is already an object in sharedPref
        jsonArray = when {
            oldJsonString.isNullOrEmpty() -> JSONArray()
            else -> JSONArray(oldJsonString)
        }

        //when editMode is active add the new entry to the JSONArray and replace it when editMode is off
        when (editMode){
            true -> {
                jsonArray.put(position, JSONObject(toJSONString()))
            }
            false -> jsonArray.put(JSONObject(toJSONString()))
        }

        //save the new object in shared_prefs
        with (sharedPref.edit()) {
            putString("geier", jsonArray.toString())
            commit()
        }
    }

    private fun toJSONString() : String {
        var json = ""
        var it = 0
        for(v in views) {
            if (0 == it) json = "{"
            when (v) {
                is EditText -> json += "\"" + v.hint + "\":\"" + v.text + "\""
            }
            when(it) {
                in 0..views.count()-2 -> json += ","
                views.count()-1 -> json += "}"
            }
            it++
        }
        return json
    }

}

