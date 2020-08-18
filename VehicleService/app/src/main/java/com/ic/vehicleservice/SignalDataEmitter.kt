package com.ic.vehicleservice

import android.util.Log
import kotlin.math.roundToInt
import kotlin.math.sin

class SignalDataEmitter: Thread() {
    private val TAG = "VehicleService"


    public override fun run() {
        generateSpeedData();
    }

    private fun generateSpeedData() {
        val Sine180 = 114.58865012930961
        val MAX_TIMES = 180.0
        val angle = 45f
        val weight: Double = 200f * 2.5 / Sine180
        val offset = 0
        var speedkmperhour = 0.0
        val sin = sin(Math.toRadians(angle.toDouble()))
        var index = offset
        while(index < MAX_TIMES + 1 + offset) {
            speedkmperhour += ((kotlin.math.sin(Math.toRadians((index * 2).toDouble())))) * weight * sin;
            speedkmperhour = speedkmperhour.roundToInt().toDouble()
            Log.i(TAG, "SignalDataEmitter $speedkmperhour")
            index++
            Thread.sleep(150)
        }
        generateSpeedData();
    }

}
