package com.example.inventur.toolbars

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.inventur.R

class OverviewToolbar : Fragment() {

    lateinit var trashButton: ImageButton
    private lateinit var callback : OnTrashClickedListener

    companion object {
        fun newInstance() : OverviewToolbar {
            val fragment = OverviewToolbar()
            return fragment
        }
    }

    interface OnTrashClickedListener {
        fun onTrashButtonClicked()
    }

    fun setOnTrashButtonClicked(callback : OnTrashClickedListener) {
        this.callback = callback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.overview_toolbar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initalize trashButton and set OnClickListener
        trashButton = view.findViewById(R.id.backButton)
        trashButton.setOnClickListener {
            callback.onTrashButtonClicked()
        }
    }

}