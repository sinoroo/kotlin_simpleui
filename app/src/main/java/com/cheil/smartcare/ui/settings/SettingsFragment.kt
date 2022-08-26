package com.cheil.smartcare.ui.settings


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cheil.smartcare.databinding.FragmentSettingsBinding
import android.app.admin.DevicePolicyManager
import android.content.Context
import com.cheil.smartcare.KioskDeviceAdminReceiver


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mDpm: DevicePolicyManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        /*

        val startLockButton: Button = binding.buttonStartLock
        val buttonEndLock: Button = binding.buttonEndLock
        startLockButton.setOnClickListener{

            val deviceAdmin = KioskDeviceAdminReceiver.getComponentName(this)
            val mDpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            if (!mDpm.isAdminActive(deviceAdmin)) {
                startLockTask()
            } else {
                // Because the package isn't allowlisted, calling startLockTask() here
                // would put the activity into screen pinning mode.
            }
        }
        */
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}