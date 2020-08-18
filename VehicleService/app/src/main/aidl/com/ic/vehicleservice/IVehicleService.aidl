// IVehicleService.aidl
package com.ic.vehicleservice;

// Declare any non-default types here with import statements
import com.ic.vehicleservice.IVehicleServiceCallback;

interface IVehicleService {
    /*
     * Subscribe for Vehicle signal type
    */
    oneway void subscribeVehicleSignal(in String type, IVehicleServiceCallback callback);
    /* Get Vehicle speed signal value */
    float getVehicleSpeed();
    /* Get Vehicle distance signal value */
    float getVehicleDistance();
    /* Unsubscribe for Vehicle signal */
    oneway void unsubscribe(in String type);
}
