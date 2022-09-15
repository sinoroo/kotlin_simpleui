package com.cheil.smartcare

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.cheil.smartcare.adapters.LoginAtteptAdapter
import com.cheil.smartcare.receivers.KioskDeviceAdminReceiver
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LoginAttemptsActivity : AppCompatActivity() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var loginAttemptsAdapter : LoginAtteptAdapter
    }

    private val networkCheck: NetworkConnection by lazy {
        NetworkConnection(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_attempts)

        supportActionBar?.hide()

        // 시작 시 관리자 권한 승인에 대해서
        // 사용자에게 확인하는 코드
        /*
        val componentName = ComponentName(this, KioskDeviceAdminReceiver::class.java)
        val policyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager

        if (policyManager.isAdminActive(componentName)) {
            Log.v("$$$$$$", "Admin is already activated")
            /*
            val lvLoginAttempts = findViewById<ListView>(R.id.lv_login_attempts)
            loginAttemptsAdapter = LoginAtteptAdapter(this)
            lvLoginAttempts.adapter = loginAttemptsAdapter
            */
        } else {
            // get dev admin permission
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                getString(R.string.device_admin_explanation))
            startActivity(intent)
        }
        */

        val fabClearAll = findViewById<FloatingActionButton>(R.id.fab_clear_all)
        fabClearAll.setOnClickListener {
            // DB 로그인
            // 추후 서버 접속 구현 시 참고
            /*
            val dbHelper = LoginAttemptsDbHelper(this)
            dbHelper.deleteAllAttempts()
            dbHelper.close()
            loginAttemptsAdapter.notifyDataSetChanged()
            */
            val nextIntent = Intent(this, MainActivity::class.java)
            startActivity(nextIntent)
        }

        // 콜백 방식의 네트워크 레지스터 클래스
        //networkCheck.register()

        if(!isNetworkAvailable(this))
        {
            val nextIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
            startActivity(nextIntent)
        }

    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw      = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false

            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                //actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                //actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // 콜백 방식의 네트워크 레지스터 클래스
        //networkCheck.unregister()
    }
}
