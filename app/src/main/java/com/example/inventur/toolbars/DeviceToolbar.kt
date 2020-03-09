package com.example.inventur.toolbars

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.inventur.R

class DeviceToolbar : Fragment() {

    lateinit var backButton: ImageButton
    private lateinit var callback : OnBackClickListener
 
    companion object {
        fun newInstance() : DeviceToolbar {
            val fragment = DeviceToolbar()
            return fragment
        }
    }

    interface OnBackClickListener {
        fun onBackButtonClicked()
    }

    fun setOnBackClickListener(callback : OnBackClickListener) {
        this.callback = callback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.device_toolbar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initalize backButton and set OnClickListener
        backButton = view.findViewById(R.id.backButton)
        backButton.setOnClickListener() {
            callback.onBackButtonClicked()
        }
    }

}