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
import kotlinx.android.synthetic.main.fragment_odometer.view.*
import kotlin.math.*

class OdoMeterFragment(private val adapter: DriverInfoPagerAdapter): Fragment(), IVehicleSignalData {

    private lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("OdoMeterFragment", "onCreateView")
        fragmentView = inflater.inflate(R.layout.fragment_odometer, container, false)
        adapter.registerVehicleSignalDataCallback(this)
        return fragmentView
    }

    override fun onVehicleSignalUpdate(type: String?, value: Double) {
        if (type == "DISTANCE") {
            fragmentView.oodometer.distanceTravelled = "%.3f".format(value).toFloat()
        }
    }
}