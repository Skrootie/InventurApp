package com.example.inventur.adapters

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.inventur.R
import com.example.inventur.enums.Overviews
import com.example.inventur.fragments.OverviewFragment
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class OverviewAdapter (private val myDataset: JSONArray, var fragment: OverviewFragment,
                       var deleteMode: Boolean, val overview: Overviews, val activity: AppCompatActivity) :
        RecyclerView.Adapter<OverviewAdapter.RecyclerViewHolder>() {

    class RecyclerViewHolder (private val view: View) : RecyclerView.ViewHolder(view) {
        val caption : TextView = view.findViewById(R.id.recyclerTextViewBig)
        val smallText : TextView = view.findViewById(R.id.recyclerTextViewSmall)
        val delButton : ImageButton = view.findViewById(R.id.delButton)

        lateinit var deletedCallback : OnDeletedClickedListener
        lateinit var itemCallback : OnItemClickedListener

        init {
            delButton.setOnClickListener {
                deletedCallback.onDelButtonClicked(adapterPosition)
            }
            view.setOnClickListener {
                itemCallback.onItemClicked(adapterPosition)
            }
        }

        fun bind(fragment: OverviewFragment) {
            setOnDeletedClickListener(fragment)
            setOnItemClicked(fragment)
        }

        interface OnDeletedClickedListener {
            fun onDelButtonClicked(position: Int)
        }

        private fun setOnDeletedClickListener(callback: OnDeletedClickedListener) {
            this.deletedCallback = callback
        }

        interface OnItemClickedListener {
            fun onItemClicked(position: Int)
        }

        private fun setOnItemClicked(callback : OnItemClickedListener) {
            this.itemCallback = callback
        }
    }

    //Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {

        //Create a new view
        val item = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_text_view , parent, false)
        return RecyclerViewHolder(item)
    }

    //Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {

        //initialize views of the current item
        when(overview) {
            //initialize the textViews in the recyclerView
            Overviews.DEVICES -> {
                //set the number of the device and its location
                val resources = activity.resources
                val jsonObject = myDataset.getJSONObject(position)
                try {
                    holder.caption.text = jsonObject.getString(resources.getString(R.string.device_number))
                    holder.smallText.text = jsonObject.getString(resources.getString(R.string.device_position))
                } catch (e: Exception) {
                    Log.i("DATA", "Error at loading device data: CORRUPTED_MEMORY \n $jsonObject")
                }
            }
            Overviews.INVENTORIES -> {
                val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
                val jsonString = sharedPref.getString(Overviews.DEVICES.toString(), "")
                val jsonObject = JSONObject(jsonString)

                //set the name of the inventory and the current device count
                holder.caption.text = myDataset.getJSONObject(position).getString("Inventurname")
                val deviceCount = when (jsonObject.getJSONArray(myDataset.getJSONObject(position).optString("Inventurname"))) {
                    //check if there is currently any device in the inventory
                    null -> 0
                    else -> jsonObject.getJSONArray(myDataset.getJSONObject(position).getString("Inventurname")).length()
                }
                holder.smallText.text = activity.applicationContext.getString(R.string.device_count, deviceCount.toString())
            }
        }
        holder.bind(fragment)

        when (deleteMode) {
            true -> holder.delButton.visibility = View.VISIBLE
            false -> holder.delButton.visibility = View.INVISIBLE
        }
    }

    //Return the size the dataset (invoked by layout manager)
    override fun getItemCount() = myDataset.length()

    fun set() {

    }
}
