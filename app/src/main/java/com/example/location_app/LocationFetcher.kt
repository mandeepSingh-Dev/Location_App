package com.example.location_app

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@SuppressLint("MissingPermission")
class LocationFetcher(val context: Context, val fusedLocationProviderClient: FusedLocationProviderClient) : LocationCallback()
{

    var isLocationAvailable = false
    var activity:Activity =  context as Activity
    var locationManger = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var isgps_enabled = false

    var permissionGranted = (context as MainActivity ).locationPermission

    var locationRequest:LocationRequest ?= null
    var locationStatusOk = false


    init {
        /** Initializing locationRequest and registering LocationCallback */
        locationRequest = LocationRequest.create()
        locationRequest?.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, this, Looper.myLooper())

    }

    override fun onLocationResult(locationResult: LocationResult?) {
        super.onLocationResult(locationResult)

        locationResult?.locations?.forEach{
            Toast.makeText(context, it.latitude.toString(), Toast.LENGTH_SHORT).show()
            Log.d("e9fineef", "onLocationResult: ${it.latitude}")

        }

    }

    override fun onLocationAvailability(locationAvailiablity: LocationAvailability?) {
        super.onLocationAvailability(locationAvailiablity)

        isLocationAvailable = locationAvailiablity?.isLocationAvailable!!
        if(permissionGranted) {
            requestLocation()
        }
    }


    fun init(){
        requestLocationPermission()
        getLastLocation()
    }

    fun requestLocationPermission()
    {
        permissionGranted = checkLocationPermission()

        if (!permissionGranted) {

          (context as BaseActivity).request_Multiple_Permission(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)){
              permissionGranted = it

              if(permissionGranted) {
                  requestLocation()
              }
          }

          //  ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),100)
        }else{
                if (!locationManger.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(context, "requireC", Toast.LENGTH_SHORT).show()
                    requestLocation()
                } else {
                    getLastLocation()
                }

        }

    }

    fun checkLocationPermission() : Boolean {
       return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocation() {
        Log.d("fsdfndgd", "requestLocation")

       //  locationRequest.setInterval(updateInterval)

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        locationRequest?.let { builder.addLocationRequest(it) }
        builder.setAlwaysShow(true)
        val locationSettingsRequest = builder.build()



        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient

        val settingsClient = LocationServices.getSettingsClient(context)
        val task = settingsClient.checkLocationSettings(locationSettingsRequest)

        task.addOnCompleteListener { task1: Task<LocationSettingsResponse?> ->
            try {
                val response =
                    task1.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location
                // requests here.

                Toast.makeText(context, "dfdnfdf", Toast.LENGTH_SHORT).show()

            } catch (exception: ApiException) {
                Log.v("Failed location ", exception.statusCode.toString())
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        // Cast to a resolvable exception.
                        val resolvable = exception as ResolvableApiException
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        try {
                            resolvable.startResolutionForResult(activity, 6)
                        } catch (e: SendIntentException) {
                            e.printStackTrace()
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.

                    }
                }
            }
        }
    }

     @SuppressLint("SuspiciousIndentation")
     fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         if (resultCode == Activity.RESULT_OK) {
          locationStatusOk = true
//             getLastLocation()
             Toast.makeText(context, "location Enabled", Toast.LENGTH_SHORT).show()
         }else{
             locationStatusOk = false
             requestLocation()
         }

     }

    fun unregisterListener()
    {
        fusedLocationProviderClient.removeLocationUpdates(this)
    }

    fun getLastLocation(){
        if( checkLocationPermission() )
        {
            fusedLocationProviderClient.lastLocation?.addOnSuccessListener {
                Toast.makeText(context, getLoc_from_latLng(it)[0].toString(), Toast.LENGTH_SHORT).show()
            }?.addOnFailureListener {

            }
        }
    }



    fun getLoc_from_latLng(location : Location) = Geocoder(context).getFromLocation(location.latitude,location.longitude,3)


    fun checkProviders(){
        val gpsProvder = locationManger.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val networkProvider = locationManger.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if(gpsProvder or networkProvider)
        {
            return
        }



    }
}


