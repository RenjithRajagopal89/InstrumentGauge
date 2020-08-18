package com.ic.vehicleservice

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.os.RemoteException
import android.util.Log
import kotlin.concurrent.thread

import com.ic.vehicleservicelib.IVehicleService
import com.ic.vehicleservicelib.IVehicleServiceCallback
import kotlin.math.roundToInt
import kotlin.math.sin

object IVehicleServiceImpl: IVehicleService.Stub() {
    private val TAG = "VehicleService"
    private val mVehicleServiceCallbacks: MutableMap<String, IVehicleServiceCallback> = mutableMapOf()

    private val mSpeedInstance : VehicleSignalGenerator = SpeedSignalGenerator(this)
    private val mDistanceInstance : VehicleSignalGenerator = DistanceSignalGenerator(this)

    private var mDistanceKm : Double = 0.0
    private var mSpeedInKmHr : Double = 0.0

    private var mServiceInitialized = false

    public fun init() {
       setServiceState(true)
    }

    public fun deInit() {
        setServiceState(false)
    }

    @Synchronized
    private fun setServiceState(state : Boolean) {
        this.mServiceInitialized = state
    }

    @Synchronized
    private fun getServiceState() : Boolean {
        return this.mServiceInitialized
    }

    private fun startSpeedSignalGenerator() {
        Log.i(TAG, "startSpeedSignalGenerator")
        // Start emitting speed
        mSpeedInstance.startGenerate()
    }

    private fun startDistanceSignalGenerator() {
        Log.i(TAG, "startDistanceSignalGenerator")
        // Start emitting distance
        mDistanceInstance.startGenerate()
    }

    public fun stopSignalGeneration() {
        Log.i(TAG,"stopSignalGeneration")
        mSpeedInstance.stopGenerate()
        mDistanceInstance.stopGenerate()

    }

    public fun onSignalGenerate(type : String, value : Double) {
        if (getServiceState()) {
            try {
                mVehicleServiceCallbacks[type]?.OnSignalChange(type, value)
                this.setSignalValue(type, value)
            } catch (e : RemoteException) {
                Log.i(TAG,"Client died ${e.message}")
                stopSignalGeneration()
            }
        }
    }

    private fun setSignalValue(type: String?, value: Double) {
        if (type == "SPEED") {
            this.setSpeed(value)
        } else if( type == "DISTANCE") {
            this.setDistance(value)
        }
    }

    private fun getSignalValue(type: String?) : Double{
        if (type == "SPEED") {
           return this.getSpeed()
        } else if( type == "DISTANCE") {
            return this.getDistance()
        }
        return 0.0
    }

    @Synchronized
    public fun getSpeed() : Double {
        return this.mSpeedInKmHr
    }

    @Synchronized
    private fun setSpeed(speedInKm:Double) {
        this.mSpeedInKmHr = speedInKm
    }

    @Synchronized
    public fun getDistance() : Double {
        return mDistanceKm
    }


    @Synchronized
    private fun setDistance(distanceInKm:Double) {
        this.mDistanceKm = distanceInKm
    }


    override fun getVehicleDistance(): Double {
       return this.getSignalValue("DISTANCE")
    }

    override fun getVehicleSpeed(): Double {
       return this.getSignalValue("SPEED")
    }

    override fun unsubscribe(type: String?) {
        if (type != null) {
            if (type.isNotEmpty()) {
                mVehicleServiceCallbacks.remove(type)
            }
        }
    }

    override fun subscribeVehicleSignal(type: String?, callback: IVehicleServiceCallback?) {
        if (callback != null && type != null) {
            mVehicleServiceCallbacks[type] = callback
            if (type == "SPEED") {
                startSpeedSignalGenerator()
            } else if ( type == "DISTANCE") {
                startDistanceSignalGenerator()
            }
        }
    }

}
