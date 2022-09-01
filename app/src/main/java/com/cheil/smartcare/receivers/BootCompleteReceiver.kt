package com.cheil.smartcare.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.cheil.smartcare.MainActivity


/* 시작 시 자동으로 앱을 실행 시키는 리시버 */
class BootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.i(TAG, "onReceive: $action")
        if (Intent.ACTION_BOOT_COMPLETED == action) {
            startActivity(context)
        }
    }

    companion object {
        const val TAG = "KIOSK"
        fun startActivity(context: Context) {
            val i = Intent(context, MainActivity::class.java)
            // For Android 9 and below
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                //context.startActivity(i, options.toBundle())
                context.startActivity(i)
            }

            // Android 10 ?
        }
    }
}