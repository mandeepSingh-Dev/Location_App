package com.example.location_app

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest

class LocationFetcher(val context: Context, val fusedLocationProviderClient: FusedLocationProviderClient) : LocationCallback()
{

    var isLocationAvailable = false
    var activity:Activity =  context as Activity
    var locationManger = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var isgps_enabled = false


    override fun onLocationResult(locationResult: LocationResult?) {
        super.onLocationResult(locationResult)
    }

    override fun onLocationAvailability(locationAvailiablity: LocationAvailability?) {
        super.onLocationAvailability(locationAvailiablity)

        isLocationAvailable = locationAvailiablity?.isLocationAvailable!!
        Log.d("dfidndfd",locationAvailiablity?.isLocationAvailable.toString())

    }


    fun checkLocationPermission()
    {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION),100)
            Toast.makeText(context,fusedLocationProviderClient.locationAvailability.toString(),Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context,fusedLocationProviderClient.locationAvailability.toString(),Toast.LENGTH_SHORT).show()
        }

        requestLocation()


    }

    fun isGPS_Enabled(){
       isgps_enabled =  locationManger.isProviderEnabled(LocationManager.GPS_PROVIDER) or locationManger.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun isGps_disbaled()
    {
    }

    interface locationListener{

        fun islocationEnabled(isEnabled:Boolean)
    }

    fun requestLocation(){

        val locationRequest = LocationRequest.create()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        val locationSettingsRequestBuilder = LocationSettingsRequest.Builder()
        locationSettingsRequestBuilder.addLocationRequest(locationRequest)
        locationSettingsRequestBuilder.setAlwaysShow(true)

        val locationse = locationSettingsRequestBuilder.build()

        val settingClient = LocationServices.getSettingsClient(context)
        settingClient.checkLocationSettings(locationse).addOnCompleteListener {

        }


    }


}


