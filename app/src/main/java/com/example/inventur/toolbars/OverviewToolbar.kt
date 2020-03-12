package com.example.inventur.toolbars

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.inventur.R

class OverviewToolbar : Fragment() {

    lateinit var trashButton: ImageButton
    lateinit var backButton: ImageButton
    lateinit var captionTextView: TextView
    var backButtonVisible = false
    lateinit var caption: String
    private lateinit var trashCallback : OnTrashClickedListener
    private lateinit var backCallback : OnBackClickListener

    companion object {
        fun newInstance(backButtonVisible: Boolean, caption: String) : OverviewToolbar {
            val fragment = OverviewToolbar()
            fragment.backButtonVisible = backButtonVisible
            fragment.caption = caption
            return fragment
        }
    }

    interface OnBackClickListener {
        fun onOverviewBackButtonClicked()
    }

    fun setOnBackButtonClicked(callback: OnBackClickListener) {
        this.backCallback = callback
    }

    interface OnTrashClickedListener {
        fun onTrashButtonClicked()
    }

    fun setOnTrashButtonClicked(callback : OnTrashClickedListener) {
        this.trashCallback = callback
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

        //initialize trashButton and set OnClickListener
        trashButton = view.findViewById(R.id.trashButton)
        trashButton.setOnClickListener {
            //trashButton.setImageResource(android.R.drawable.ic_menu_save)
            trashCallback.onTrashButtonClicked()
        }

        captionTextView = view.findViewById(R.id.caption)
        captionTextView.text = caption

        backButton = view.findViewById(R.id.backButton)
        if (!backButtonVisible) {
            backButton.visibility = View.INVISIBLE
            captionTextView.textAlignment = View.TEXT_ALIGNMENT_INHERIT
        }
        backButton.setOnClickListener {
            backCallback.onOverviewBackButtonClicked()
        }
    }

}