package com.ic.driverinformation.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ic.driverinformation.view.OdoMeterFragment
import com.ic.driverinformation.view.SpeedoMeterFragment
import com.ic.vehicleservicelib.IVehicleServiceCallback

class DriverInfoPagerAdapter(fragmentManager: FragmentManager):
    FragmentStatePagerAdapter(fragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var callbacks = mutableListOf<IVehicleSignalData>()

    override fun getItem(position: Int): Fragment {
        if (position == 0) {
            return SpeedoMeterFragment(this)
        }
        return OdoMeterFragment(this)
    }

    override fun getCount(): Int {
        return 2
    }

    fun registerVehicleSignalDataCallback(callback: IVehicleSignalData) {
        callbacks.add(callback)
    }

    fun onVehicleSignalUpdate(type: String?, value: Double) {
        for (callback in callbacks) {
            callback.onVehicleSignalUpdate(type, value)
        }
    }
}