package com.cheil.smartcare.receivers

import android.annotation.SuppressLint
import android.app.admin.DeviceAdminReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.UserHandle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.util.*

class KioskDeviceAdminReceiver : DeviceAdminReceiver() {

    companion object{
        fun getComponentName(context:Context):ComponentName{
            Log.d("Component Name",ComponentName(context.applicationContext,
                KioskDeviceAdminReceiver::class.java).toString())
            return ComponentName(context.applicationContext,
                KioskDeviceAdminReceiver::class.java)
        }
    }

    //lateinit var dbHelper: LoginAttemptsDbHelper

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun getCurrentDateTime(): String {
        val currentTimeInMillis: Long = System.currentTimeMillis()

        val df: SimpleDateFormat = SimpleDateFormat("dd MMM, yyyy hh:mm a")
        return df.format(Date(currentTimeInMillis))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onPasswordSucceeded(context: Context, intent: Intent, user: UserHandle) {
        /*
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("AttemptInfo", Context.MODE_PRIVATE)
        var id = sharedPreferences.getLong("attemptId",0)
        val attemptTime = getCurrentDateTime()

        dbHelper = LoginAttemptsDbHelper(context)
        dbHelper.insertAttempt(LoginAttemptModel(id, LoginAttemptModel.SUCCESS, attemptTime))

        LoginAttemptsActivity.loginAttemptsAdapter.notifyDataSetChanged()

        Log.v("$$$$$$", "Password Succeeded")

        id++

        val editor = sharedPreferences.edit()
        editor.putLong("attemptId", id)
        editor.apply()
        */
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onPasswordFailed(context: Context, intent: Intent, user: UserHandle) {
        /*
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("AttemptInfo", Context.MODE_PRIVATE)
        var id = sharedPreferences.getLong("attemptId",0)
        val attemptTime = getCurrentDateTime()


        dbHelper = LoginAttemptsDbHelper(context)
        dbHelper.insertAttempt(LoginAttemptModel(id, LoginAttemptModel.FAILED, attemptTime))

        LoginAttemptsActivity.loginAttemptsAdapter.notifyDataSetChanged()

        Log.v("$$$$$$", "Password Failed")

        id++

        val editor = sharedPreferences.edit()
        editor.putLong("attemptId", id)
        editor.apply()
         */
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onEnabled(context: Context, intent: Intent) {
        //super.onEnabled(context, intent)
        Toast.makeText( context,"Enabled", Toast.LENGTH_SHORT)
    }

    override fun onDisabled(context: Context, intent: Intent) {
        //super.onDisabled(context, intent)
        Toast.makeText( context,"Disabled", Toast.LENGTH_SHORT)
    }

}
