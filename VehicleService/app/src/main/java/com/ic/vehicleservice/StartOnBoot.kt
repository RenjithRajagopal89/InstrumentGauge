package com.ic.vehicleservice

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process.SYSTEM_UID
import android.os.UserHandle
import android.util.Log
import androidx.annotation.RequiresApi


class StartOnBoot : BroadcastReceiver() {

    private val TAG = "VehicleService"
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        Log.v(TAG, "Intent received: " + intent.action)
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val serviceIntent =
                Intent(context, com.ic.vehicleservice.VehicleService::class.java)
            context.startForegroundService(serviceIntent)

        }
    }
}