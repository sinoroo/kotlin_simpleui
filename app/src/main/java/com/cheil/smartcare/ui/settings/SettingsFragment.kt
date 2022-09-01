package com.cheil.smartcare.ui.settings


import android.app.admin.DevicePolicyManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cheil.smartcare.MainActivity
import com.cheil.smartcare.databinding.FragmentSettingsBinding
import com.google.android.material.snackbar.Snackbar


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mDpm: DevicePolicyManager
    private var mIsKioskEnabled = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val startLockButton: Button = binding.buttonStartLock
        startLockButton.setOnClickListener{ view ->
            Snackbar.make(view, "StartLock", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            (activity as MainActivity).startLockTask()
            (activity as MainActivity).hideSystemUI()
            mIsKioskEnabled = true
        }

        val buttonEndLock: Button = binding.buttonEndLock
        buttonEndLock.setOnClickListener { view ->
            Snackbar.make(view, "EndLock", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            (activity as MainActivity).stopLockTask()
            (activity as MainActivity).showSystemUI()
            mIsKioskEnabled = false
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}