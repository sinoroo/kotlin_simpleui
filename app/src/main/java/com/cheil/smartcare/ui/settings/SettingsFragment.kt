package com.cheil.smartcare.ui.settings


import android.app.admin.DevicePolicyManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cheil.smartcare.BleDeviceScanActivity
import com.cheil.smartcare.MainActivity
import com.cheil.smartcare.databinding.FragmentSettingsBinding
import com.google.android.material.snackbar.Snackbar


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val REQUEST_ENABLE_BT=1

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
            //(activity as MainActivity).hideSystemUI()
            mIsKioskEnabled = true
        }

        val buttonEndLock: Button = binding.buttonEndLock
        buttonEndLock.setOnClickListener { view ->
            Snackbar.make(view, "EndLock", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            (activity as MainActivity).stopLockTask()
            //(activity as MainActivity).showSystemUI()
            mIsKioskEnabled = false
        }

        val callWifiSettings: Button = binding.callWifiSettings
        callWifiSettings.setOnClickListener{ view ->
            val nextIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
            startActivity(nextIntent)
        }

        val bluetoothManager: BluetoothManager = (activity as MainActivity).getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText((activity as MainActivity), "not support Bluetooth", Toast.LENGTH_SHORT).show()
        }
        else {
            if (bluetoothAdapter?.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
            val callBluetoothSettings: Button = binding.callBluetoothSettings
            callBluetoothSettings.setOnClickListener { view ->
                val nextIntent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                startActivity(nextIntent)
            }
        }

        val callScanBle: Button = binding.callScanBle
        callScanBle.setOnClickListener{ view ->
            val nextIntent = Intent(activity as MainActivity, BleDeviceScanActivity::class.java)
            startActivity(nextIntent)
        }
        return root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}