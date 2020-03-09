package com.example.inventur.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventur.R
import com.example.inventur.adapters.OverviewAdapter
import org.json.JSONArray
import org.json.JSONObject

class OverviewFragment : Fragment(), OverviewAdapter.RecyclerViewHolder.OnDeletedClickedListener, OverviewAdapter.RecyclerViewHolder.OnItemClickedListener {

    private lateinit var addButton : ImageButton
    private lateinit var addCallback : OnAddClickListener
    private lateinit var recyclerItemCallback : OnRecyclerItemClickListener
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter : RecyclerView.Adapter<*>
    private lateinit var viewManager : RecyclerView.LayoutManager
    private lateinit var ctx : Context
    private lateinit var jsonArray: JSONArray
    private var deleteMode = false

    companion object {
        fun newInstance() : OverviewFragment {
            val fragment = OverviewFragment()
            return fragment
        }
    }

    fun setOnAddClickListener(callback: OnAddClickListener) {
        this.addCallback = callback
    }

    interface OnAddClickListener {
        fun onAddButtonClicked()
    }

    fun setOnItemClickListener(callback: OnRecyclerItemClickListener) {
        this.recyclerItemCallback = callback
    }

    interface OnRecyclerItemClickListener {
        fun onRecyclerItemClicked(jsonObject: JSONObject, position: Int)
    }

    //#######################################################
    //################### View functions ####################
    //#######################################################

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        ctx = activity!!.applicationContext


        //Load the devices' data out of sharedPreferences
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val defaultValue = ""
        var jsonString = sharedPref!!.getString("geier", defaultValue)
        if (jsonString!!.isEmpty()) jsonString = "[]"
        jsonArray = JSONArray(jsonString)

        //Initialize viewManager and viewAdapter for the RecyclerView
        viewManager = LinearLayoutManager(ctx)
        viewAdapter = OverviewAdapter(jsonArray, this, deleteMode)

        return inflater?.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initialize recyclerView
        recyclerView = view.findViewById<RecyclerView>(R.id.deviceView).apply  {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        //initialize the addButton and set onClickListener    ,
        addButton = view.findViewById(R.id.addButton)
        addButton.setOnClickListener {
            addCallback.onAddButtonClicked()
        }

        when (deleteMode) {
            true -> addButton.visibility = View.INVISIBLE
            false -> addButton.visibility = View.VISIBLE
        }
    }

    //#######################################################
    //################### OnClickListeners ##################
    //#######################################################

    override fun onDelButtonClicked(position: Int) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        jsonArray.remove(position)
        with (sharedPref!!.edit()) {
            putString("geier", jsonArray.toString())
            commit()
        }
        var transaction = fragmentManager!!.beginTransaction()
        transaction.detach(this).attach(this).commit()
    }

    override fun onItemClicked(position: Int) {
        recyclerItemCallback.onRecyclerItemClicked(jsonArray.getJSONObject(position), position)
    }

    //#########################################################
    //################### internal functions ##################
    //#########################################################

    fun switchDeleteMode() {
        deleteMode = when (deleteMode) {
            true -> false
            false -> true
        }
        var transaction = fragmentManager!!.beginTransaction()
        transaction.detach(this).attach(this).commit()
    }

    fun reset() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        with (sharedPref!!.edit()) {
            putString("geier", "")
            commit()
        }
    }
}