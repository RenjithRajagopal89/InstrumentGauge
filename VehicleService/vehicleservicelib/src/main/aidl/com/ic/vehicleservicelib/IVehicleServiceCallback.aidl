// IVehicleServiceCallback.aidl
package com.ic.vehicleservicelib;

interface IVehicleServiceCallback {
    oneway void OnSignalChange(in String type, double signalValue);
}
