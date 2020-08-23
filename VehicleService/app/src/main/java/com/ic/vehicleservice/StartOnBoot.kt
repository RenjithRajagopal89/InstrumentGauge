package com.ic.vehicleservice

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process.SYSTEM_UID
import android.os.UserHandle
import android.util.Log
import androidx.annotation.RequiresApi


class StartOnBoot : BroadcastReceiver() {

    private val TAG = "VehicleService"
    private val VEHICLE_SERVICE_PACKAGE = "com.ic.vehicleservice"
    private val VEHICLE_SERVICE_INTERFACE_NAME = "com.ic.vehicleservice.IVehicleService"

    override fun onReceive(context: Context, intent: Intent) {
        Log.v(TAG, "Intent received: " + intent.action)
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val serviceIntent = Intent(VEHICLE_SERVICE_INTERFACE_NAME)
            serviceIntent.setComponent(ComponentName(VEHICLE_SERVICE_PACKAGE,
                "$VEHICLE_SERVICE_PACKAGE.VehicleService"
            ))
            context.startForegroundService(serviceIntent)

        }
    }
}