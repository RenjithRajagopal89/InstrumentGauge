package com.ic.vehicleservicelib

interface VehicleServiceStatusListener {
    fun OnSignalUpdate(type: String?, value: Double)
}