package com.ic.driverinformation.adapters

interface IVehicleSignalData {
    fun onVehicleSignalUpdate(type: String?, value: Double)
}