// IVehicleService.aidl
package com.ic.vehicleservicelib;

// Declare any non-default types here with import statements
import com.ic.vehicleservicelib.IVehicleServiceCallback;

interface IVehicleService {
    /*
     * Subscribe for Vehicle signal type
    */
    oneway void subscribeVehicleSignal(in String type, IVehicleServiceCallback callback);
    /* Get Vehicle speed signal value */
    double getVehicleSpeed();
    /* Get Vehicle distance signal value */
    double getVehicleDistance();
    /* Unsubscribe for Vehicle signal */
    oneway void unsubscribe(in String type);
}
