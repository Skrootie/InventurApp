package com.example.inventur.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventur.R
import com.example.inventur.fragments.OverviewFragment
import org.json.JSONArray

class OverviewAdapter (private val myDataset: JSONArray, var fragment: OverviewFragment, var deleteMode: Boolean) :
        RecyclerView.Adapter<OverviewAdapter.RecyclerViewHolder>() {

    class RecyclerViewHolder (private val view: View) : RecyclerView.ViewHolder(view) {
        val deviceNumber : TextView = view.findViewById(R.id.recyclerTextViewBig)
        val location : TextView = view.findViewById(R.id.recyclerTextViewSmall)
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

        fun setOnItemClicked(callback : OnItemClickedListener) {
            this.itemCallback = callback
        }
    }

    //Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OverviewAdapter.RecyclerViewHolder {

        //Create a new view
        val item = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_text_view , parent, false)
        return RecyclerViewHolder(item)
    }

    //Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {

        //initialize views of the current item
        holder.deviceNumber.text = myDataset.getJSONObject(position).getString("Gerätenummer")
        holder.location.text = myDataset.getJSONObject(position).getString("Position")
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
