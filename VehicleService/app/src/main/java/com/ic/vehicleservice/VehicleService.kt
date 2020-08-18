package com.ic.vehicleservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN


class VehicleService : Service() {
    private var TAG = "VehicleService"
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        Log.i(TAG, "Service onCreate")
        super.onCreate()
        startForeGroundService()
        IVehicleServiceImpl.init()
    }

    private fun startForeGroundService() {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "Background Service")
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        val notificationBuilder = NotificationCompat.Builder(this, channelId )
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(101, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    override fun onDestroy() {
        Log.i(TAG, "Service onDestroy")
        super.onDestroy()
        IVehicleServiceImpl.deInit()
        IVehicleServiceImpl.stopSignalGeneration()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i(TAG, "Service onStartCommand")
        /* Tells the OS to recreate the service after it has enough memory */return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return IVehicleServiceImpl
    }
}