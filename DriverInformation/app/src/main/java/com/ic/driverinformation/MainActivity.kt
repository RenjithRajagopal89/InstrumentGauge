package com.ic.driverinformation

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ic.driverinformation.adapters.DriverInfoPagerAdapter
import com.ic.vehicleservicelib.VehicleServiceApi
import kotlinx.android.synthetic.main.activity_main.*

import com.ic.vehicleservicelib.VehicleServiceApiConnectionCb
import com.ic.vehicleservicelib.VehicleServiceStatusListener

class MainActivity : AppCompatActivity() {

    private val TAG = "DriverInformation"
    private lateinit var pagerAdapter: DriverInfoPagerAdapter
    private var mVehicleServiceApi: VehicleServiceApi? = null


    private val mVehicleServiceStatusListener: VehicleServiceStatusListener =
        object : VehicleServiceStatusListener {
            override fun OnSignalUpdate(type: String?, value: Double) {
                pagerAdapter.onVehicleSignalUpdate(type, value)
            }
        }

    private val mVehicleConnectionCb: VehicleServiceApiConnectionCb = object :  VehicleServiceApiConnectionCb {
        override fun onServiceConnected() {
           Log.i(TAG, "onServiceConnected")
            mVehicleServiceApi?.subscribeVehicleSignalUpdates("SPEED", mVehicleServiceStatusListener)
            mVehicleServiceApi?.subscribeVehicleSignalUpdates("DISTANCE", mVehicleServiceStatusListener)
        }

        override fun onServiceDisconnected() {
            Log.i(TAG, "onServiceDisconnected")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        pagerAdapter = DriverInfoPagerAdapter(supportFragmentManager)
        info_viewpager.adapter = pagerAdapter

        mVehicleServiceApi = VehicleServiceApi(this,mVehicleConnectionCb)
    }



    override fun onStart() {
        super.onStart()

    }

    override fun onDestroy() {
        super.onDestroy()
        mVehicleServiceApi?.unSubscribeVehicleSignalUpdates("SPEED")
        mVehicleServiceApi?.unSubscribeVehicleSignalUpdates("DISTANCE")
        mVehicleServiceApi?.disconnect()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}
