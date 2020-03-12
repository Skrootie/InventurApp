package com.example.inventur.toolbars

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.inventur.R

class EntryToolbar : Fragment() {

    lateinit var backButton: ImageButton
    lateinit var saveButton : ImageButton
    private lateinit var backCallback : OnBackClickListener
    private lateinit var saveCallback : OnSaveClickListener

    companion object {
        fun newInstance() : EntryToolbar {
            val fragment = EntryToolbar()
            return fragment
        }
    }

    fun setOnSaveClickListener(callback: OnSaveClickListener) {
        this.saveCallback = callback
    }

    interface OnSaveClickListener {
        fun onSaveButtonClicked()
    }

    interface OnBackClickListener {
        fun onEntryBackButtonClicked()
    }

    fun setOnBackClickListener(callback : OnBackClickListener) {
        this.backCallback = callback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.entry_toolbar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initialize backButton and saveButton and set OnClickListener
        backButton = view.findViewById(R.id.trashButton)
        backButton.setOnClickListener() {
            backCallback.onEntryBackButtonClicked()
        }
        saveButton = view.findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            saveCallback.onSaveButtonClicked()
        }
    }

}