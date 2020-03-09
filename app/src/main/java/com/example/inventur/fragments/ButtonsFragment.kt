package com.example.inventur.fragments

import android.view.View
import androidx.fragment.app.ListFragment

class ButtonsFragment : ListFragment() {
    internal lateinit var callback: OnClickListener

    fun setOnClickListener(callback: OnClickListener) {
        this.callback = callback
    }

    interface OnClickListener {
        fun onButtonClicked()
    }
}