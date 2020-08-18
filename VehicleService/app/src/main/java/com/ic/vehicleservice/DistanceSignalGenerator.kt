package com.ic.vehicleservice

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import kotlin.concurrent.thread

class DistanceSignalGenerator : VehicleSignalGenerator {

    private val TAG = "DistanceSignalGenerator"
    private lateinit var mHandlerDistance : Handler
    private var mDistanceKm : Double = 0.0
    private var mVehicleServiceImplInstance : IVehicleServiceImpl
    private lateinit var handlerThreadDistance : HandlerThread

    private var mRunningState = true

    constructor(vehicleInstance : IVehicleServiceImpl) {
        this.mVehicleServiceImplInstance = vehicleInstance
    }


    override fun startGenerate() {
        setRunningState(true)
        startDistanceEmitterThread()
    }

    override fun stopGenerate() {
        setRunningState(false)
        mHandlerDistance.removeCallbacksAndMessages(mHandlerDistance)
        handlerThreadDistance.quitSafely()
    }


    private fun startDistanceEmitterThread() {
        handlerThreadDistance = HandlerThread("DistanceSignalEmitter")
        handlerThreadDistance.start()
        mHandlerDistance = object : Handler(handlerThreadDistance.looper) {
            override fun handleMessage(msg: Message) {
                mVehicleServiceImplInstance.onSignalGenerate("DISTANCE", msg.obj as Double)
            }
        }

        thread {
            generateDistanceData()
        }
    }

    private fun generateDistanceData() {
        var distanceInKm = 0.0
        while(getRunningState()) {
            distanceInKm += (mVehicleServiceImplInstance.getSpeed()/3600)
            Thread.sleep(500)
            mHandlerDistance.sendMessage(Message.obtain(mHandlerDistance, 0, distanceInKm))
        }
    }

    @Synchronized
    public fun getDistance() : Double {
        return mDistanceKm
    }


    @Synchronized
    private fun setDistance(distanceInKm:Double) {
        this.mDistanceKm = distanceInKm
    }

    @Synchronized
    private fun getRunningState() : Boolean {
        return mRunningState
    }

    @Synchronized
    private fun setRunningState(state : Boolean) {
        this.mRunningState = state
    }

}