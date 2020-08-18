// IVehicleServiceCallback.aidl
package com.ic.vehicleservice;

interface IVehicleServiceCallback {
    oneway void OnSignalChange(in String type, float signalValue);
}
