package com.ic.vehicleservicelib

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log


class VehicleServiceApi : ServiceConnection {

    private val LOG_TAG = "VehicleServiceApi"
    private val LOG_PREFIX = "[VehicleServiceApi]"
    private var context: Context? = null
    private val VEHICLE_SERVICE_PACKAGE = "com.ic.vehicleservice"
    private val VEHICLE_SERVICE_INTERFACE_NAME = "com.ic.vehicleservice.IVehicleService"

    private var vehicleServiceApi: IVehicleService? = null
    private var vehicleServiceApiConnectionCb: VehicleServiceApiConnectionCb? = null
    private var mVehicleServiceStatusListener: VehicleServiceStatusListener? = null

    private var serviceBound = false

    constructor(context: Context?, vehicleServiceApiConnectionCb: VehicleServiceApiConnectionCb?) {
        this.context = context
        this.vehicleServiceApiConnectionCb = vehicleServiceApiConnectionCb
        connect()
    }

    private fun connect() {
        Log.d(LOG_TAG,
            "Connect")
       // val intent = Intent()
       // intent.action = "AnalyticsCloudApi"
        //intent.component = ComponentName(PACKAGENAME, PACKAGENAME_SERVICENAME)
        val intent = Intent()
        intent.setPackage(VEHICLE_SERVICE_PACKAGE)
        intent.action = VEHICLE_SERVICE_INTERFACE_NAME
        context!!.bindService(intent, this, Context.BIND_AUTO_CREATE)
        Log.d(LOG_TAG, LOG_PREFIX.toString() + "Binding successful")
    }


    fun disconnect() {
        if (serviceBound) {
            context!!.unbindService(this)
        }
    }

    private val mVehicleServiceCallback: IVehicleServiceCallback = object : IVehicleServiceCallback.Stub() {
        override fun OnSignalChange(type: String?, signalValue: Double) {
           mVehicleServiceStatusListener?.OnSignalUpdate(type, signalValue)
        }
    }

    fun  subscribeVehicleSignalUpdates(signaltype : String , callback:VehicleServiceStatusListener ): Boolean {
        mVehicleServiceStatusListener = callback
        try {
            vehicleServiceApi?.subscribeVehicleSignal(signaltype, mVehicleServiceCallback)
        } catch (e: RemoteException) {
            Log.e(LOG_TAG, "$LOG_PREFIX Error caught in subscribeVehicleSignalUpdates", e)
            return false
        }
        return true
    }

    fun unSubscribeVehicleSignalUpdates(signaltype: String): Boolean {
        try {
            vehicleServiceApi?.unsubscribe(signaltype)
        } catch (e: RemoteException) {
            Log.e(LOG_TAG, "$LOG_PREFIX Error caught in unSubscribeVehicleSignalUpdates", e)
            return false
        }
        return true
    }

    fun getVehicleSpeed() : Double? {
        return vehicleServiceApi?.vehicleSpeed
    }

    fun getVehicleDistance() : Double? {
        return vehicleServiceApi?.vehicleDistance
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        serviceBound = false;
        Log.d(LOG_TAG, LOG_PREFIX + "onServiceDisconnected()");
        vehicleServiceApiConnectionCb?.onServiceDisconnected()
    }


    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        Log.d(LOG_TAG, LOG_PREFIX + "onServiceConnected()")
        vehicleServiceApi = IVehicleService.Stub.asInterface(p1)
        vehicleServiceApiConnectionCb?.onServiceConnected();
    }


}