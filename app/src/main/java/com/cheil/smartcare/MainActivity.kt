package com.cheil.smartcare

import android.app.PendingIntent
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.cheil.smartcare.databinding.ActivityMainBinding
import com.cheil.smartcare.receiver.KioskDeviceAdminReceiver
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    var PACKAGE_NAME: String? = null
    private lateinit var mDpm: DevicePolicyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        PACKAGE_NAME = applicationContext.packageName

        // 상단 툴바 제거
        supportActionBar?.hide()
        //supportActionBar!!.hide()

        val deviceAdmin = KioskDeviceAdminReceiver.getComponentName(this)
        mDpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        if (!mDpm.isAdminActive(deviceAdmin)) {
            Toast.makeText(this, "not device admin", Toast.LENGTH_SHORT).show()
            //val nextIntent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            //nextIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdmin)
            //startActivity(nextIntent)
        }

        if (mDpm.isDeviceOwnerApp(packageName)) {
            mDpm.setLockTaskPackages(deviceAdmin, arrayOf(packageName))
        } else {
            Toast.makeText(this, "not device owner", Toast.LENGTH_SHORT).show()
        }
        if (mDpm.isLockTaskPermitted(packageName)) {
            startLockTask()
        }

        binding.appBarMain.fab?.setOnClickListener { view ->
            Snackbar.make(view, "Voice Recognition", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?)!!
        val navController = navHostFragment.navController

        binding.navView?.let {
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_transform, R.id.nav_reflow, R.id.nav_slideshow, R.id.nav_settings
                ),
                binding.drawerLayout
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            it.setupWithNavController(navController)
        }

        binding.appBarMain.contentMain.bottomNavView?.let {
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_transform, R.id.nav_reflow, R.id.nav_slideshow
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            it.setupWithNavController(navController)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val result = super.onCreateOptionsMenu(menu)
        // Using findViewById because NavigationView exists in different layout files
        // between w600dp and w1240dp
        val navView: NavigationView? = findViewById(R.id.nav_view)
        if (navView == null) {
            // The navigation drawer already has the items including the items in the overflow menu
            // We only inflate the overflow menu if the navigation drawer isn't visible
            menuInflater.inflate(R.menu.overflow, menu)
        }
        return result
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_settings -> {
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                navController.navigate(R.id.nav_settings)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}