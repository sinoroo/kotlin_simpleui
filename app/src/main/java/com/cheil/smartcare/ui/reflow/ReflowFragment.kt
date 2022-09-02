package com.cheil.smartcare.ui.reflow

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cheil.smartcare.BleDeviceScanActivity
import com.cheil.smartcare.MainActivity
import com.cheil.smartcare.R
import com.cheil.smartcare.databinding.FragmentReflowBinding


class ReflowFragment : Fragment() {

    private var _binding: FragmentReflowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val reflowViewModel =
            ViewModelProvider(this).get(ReflowViewModel::class.java)

        _binding = FragmentReflowBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val textView: TextView = binding.textReflow
        reflowViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val bleScanBtn: Button = binding.moveBleScanBtn

        //val bleScanBtn: Button = findViewById(R.id.move_ble_scan_btn)

        bleScanBtn.setOnClickListener {
            val nextIntent = Intent(activity as MainActivity, BleDeviceScanActivity::class.java)
            startActivity(nextIntent)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}