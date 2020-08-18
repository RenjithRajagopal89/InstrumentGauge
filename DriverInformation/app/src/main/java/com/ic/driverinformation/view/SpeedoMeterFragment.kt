package com.ic.driverinformation.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ic.driverinformation.R
import com.ic.driverinformation.adapters.DriverInfoPagerAdapter
import com.ic.driverinformation.adapters.IVehicleSignalData
import kotlinx.android.synthetic.main.fragment_speedometer.view.*

class SpeedoMeterFragment(private val adapter: DriverInfoPagerAdapter): Fragment(), IVehicleSignalData {

    private lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("SpeedoMeterFragment", "onCreateView")
        fragmentView = inflater.inflate(R.layout.fragment_speedometer, container, false)
        adapter.registerVehicleSignalDataCallback(this)
        return fragmentView
    }

    override fun onVehicleSignalUpdate(type: String?, value: Double) {
        if (type == "SPEED") {
            fragmentView.speedometer.currentSpeed = value.toFloat()
        }
    }
}