package com.cheil.smartcare

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.cheil.smartcare.adapters.LoginAttemptsDbHelper
import com.cheil.smartcare.adapters.LoginAtteptAdapter
import com.cheil.smartcare.receivers.KioskDeviceAdminReceiver
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LoginAttemptsActivity : AppCompatActivity() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var loginAttemptsAdapter : LoginAtteptAdapter
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_attempts)

        supportActionBar?.hide()

        val componentName = ComponentName(this, KioskDeviceAdminReceiver::class.java)
        val policyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager

        if (policyManager.isAdminActive(componentName)) {
            Log.v("$$$$$$", "Admin is already activated")

            val lvLoginAttempts = findViewById<ListView>(R.id.lv_login_attempts)
            loginAttemptsAdapter = LoginAtteptAdapter(this)
            lvLoginAttempts.adapter = loginAttemptsAdapter

        } else {
            // get dev admin permission
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                getString(R.string.device_admin_explanation))
            startActivity(intent)
        }

        val fabClearAll = findViewById<FloatingActionButton>(R.id.fab_clear_all)
        fabClearAll.setOnClickListener {
            val dbHelper = LoginAttemptsDbHelper(this)
            dbHelper.deleteAllAttempts()
            dbHelper.close()
            loginAttemptsAdapter.notifyDataSetChanged()

        }
    }


}