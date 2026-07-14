package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class MotivationFragment : Fragment(R.layout.fragment_motivation) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tv = view.findViewById<TextView>(R.id.tvFragmentText)
        val btn = view.findViewById<Button>(R.id.btnNotifyParent)

        // Communicating with Parent Activity
        btn.setOnClickListener {
            (activity as? MainActivity)?.onFragmentMessage("Fragment says: Keep training hard!")
        }
    }
}