package com.ic.vehicleservice

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import java.lang.IllegalStateException
import java.lang.RuntimeException
import kotlin.concurrent.thread
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.system.exitProcess

class SpeedSignalGenerator() : VehicleSignalGenerator {

    private val TAG = "SpeedSignalGenerator"
    private lateinit var mHandlerSpeed : Handler
    private lateinit var mVehicleServiceImplInstance : IVehicleServiceImpl
    private var mSpeedKmHr : Double = 0.0
    private var mRunningState = true

    private lateinit var handlerThreadSpeed : HandlerThread

    constructor(vehicleInstance : IVehicleServiceImpl) : this() {
        this.mVehicleServiceImplInstance = vehicleInstance
    }

    override fun startGenerate() {
        setRunningState(true)
        startSpeedEmitterThread()
    }

    override fun stopGenerate() {
        Log.i(TAG, "stopGenerate")
        setRunningState(false)
        mHandlerSpeed.removeCallbacksAndMessages(mHandlerSpeed)
        handlerThreadSpeed.quitSafely()

    }

    private fun startSpeedEmitterThread() {
        handlerThreadSpeed = HandlerThread("SpeedSignalEmitter")
        handlerThreadSpeed.start()
        mHandlerSpeed = object : Handler(handlerThreadSpeed.looper) {
            override fun handleMessage(msg: Message) {
                mVehicleServiceImplInstance.onSignalGenerate("SPEED", msg.obj as Double)
            }
        }

        thread {
            this.generateSpeedData()
        }

        Log.i(TAG, "startSpeedEmitterThread -")
    }

    private fun generateSpeedData() {

        val sine_to_180 = 114.58865012930961
        val max_times = 180.0
        val angle = 45f
        val weight: Double = 200f * 2.5 / sine_to_180
        val offset = 0
        val sin = sin(Math.toRadians(angle.toDouble()))
        while (getRunningState()) {
            var index = offset
            var speedkmperhour = 0.0
            while (index < max_times + 1 + offset) {
                speedkmperhour += ((kotlin.math.sin(Math.toRadians((index * 2).toDouble())))) * weight * sin
                speedkmperhour = speedkmperhour.roundToInt().toDouble()
                this.setSpeed(speedkmperhour)
                if( mHandlerSpeed.looper.thread.isAlive) {
                    // Post event to Main Message Queue
                    mHandlerSpeed.sendMessage(Message.obtain(mHandlerSpeed, 0, speedkmperhour))
                } else {
                    break
                }
                index++
                Thread.sleep(150)
            }
        }
    }

    @Synchronized
    public fun getSpeed() : Double {
        return this.mSpeedKmHr
    }

    @Synchronized
    private fun setSpeed(speedInKm:Double) {
        this.mSpeedKmHr = speedInKm
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