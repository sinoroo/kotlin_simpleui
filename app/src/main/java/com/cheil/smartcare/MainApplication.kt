package com.cheil.smartcare

import android.app.Application
import com.cheil.smartcare.MainApplication
import android.content.Intent
import com.cheil.smartcare.MainActivity
import android.app.PendingIntent
import android.app.AlarmManager
import android.util.Log

class MainApplication : Application() {
    var defaultUncaughtExceptionHandler: Thread.UncaughtExceptionHandler? = null
        private set

    override fun onCreate() {
        super.onCreate()
        defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Log.d("MainApplication", "" + defaultUncaughtExceptionHandler)
        Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler())
    }

    private inner class UncaughtExceptionHandler : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread, e: Throwable) {
            Log.d(TAG, "Crash!!!")
            val restartIntent = Intent(applicationContext, MainActivity::class.java)
            val runner = PendingIntent.getActivity(
                applicationContext,
                99,
                restartIntent,
                PendingIntent.FLAG_ONE_SHOT
            )
            val am = getSystemService(ALARM_SERVICE) as AlarmManager
            am[AlarmManager.ELAPSED_REALTIME_WAKEUP, 5000] = runner

            // Try everything to make sure this process goes away.
            // android.os.Process.killProcess(android.os.Process.myPid());
            // System.exit(10);
            defaultUncaughtExceptionHandler!!.uncaughtException(t, e)
        }
    }

    companion object {
        private val TAG = MainApplication::class.java.simpleName
    }
}