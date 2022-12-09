package com.example.location_app.Utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.os.Process
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.location_app.Base.BaseActivity
import com.example.location_app.BuildConfig
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import java.util.*


@SuppressLint("MissingPermission")
class LocationFetcher(val context: Context,val myListtener: MyListener) : LocationCallback()
{

    var isLocationAvailable = false
    var activity:Activity =  context as Activity
    var locationManger = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    var permissionGranted = false

    var locationRequest:LocationRequest ?= LocationRequest.create()
    var locationStatusOk = false

    var  currentlocation : Location?= null

    lateinit var geocoder: Geocoder

    lateinit var myListener: MyListener
    lateinit var fusedLocationProviderClient : FusedLocationProviderClient

    interface MyListener {
        fun onLocationChanged(location: Location)
    }
    init {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)


        /*  fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
          *//** Initializing locationRequest and registering LocationCallback *//*
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 3000L
         //   smallestDisplacement = 5F
        }*/


    /*    startLocationUpdates()*/
       // fusedLocationProviderClient.requestLocationUpdates(locationRequest, this, Looper.myLooper())


   /*     geocoder = Geocoder(context)

        myListener = myListtener*/


    }

    private fun startLocationUpdates() {

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, this, Looper.myLooper())

    }

    override fun onLocationResult(locationResult: LocationResult?) {
        super.onLocationResult(locationResult)

        showToast("onLocationResult")
        Log.d("LL", "onLocationResult: ${locationResult?.locations?.lastOrNull()} ")
        locationResult?.lastLocation?.let { loc ->
            currentlocation = loc

            val addressList= currentlocation?.let {  getLoc_from_latLng(it) }
            addressList?.get(0)?.let { it1 -> showToast( " ${it1.subLocality} ${ currentlocation?.latitude } ") }

            currentlocation?.let {
                myListener.onLocationChanged(it)
            }
        }
    }

    override fun onLocationAvailability(locationAvailiablity: LocationAvailability?) {
        super.onLocationAvailability(locationAvailiablity)

        isLocationAvailable = locationAvailiablity?.isLocationAvailable!!
        if(! isLocationAvailable) {
            showToast("Location Not Available")
        }else{
            showToast("Location Available")
        }
        if(permissionGranted) {
            requestLocation()
        }
    }


    fun init(){

        /** Initializing locationRequest and registering LocationCallback */
        locationRequest?.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 3000L
            //   smallestDisplacement = 5F
        }


        startLocationUpdates()

        geocoder = Geocoder(context)

        myListener = myListtener

        requestLocationPermission()
        // TODO : FETCH CURRENT LOCATION HERE
           getLastLocation()

    }

    fun requestLocationPermission()
    {
        permissionGranted = checkLocationPermission()

        if (!permissionGranted) {


          (context as BaseActivity).request_Multiple_Permission(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)){
              permissionGranted = it

              Constants._isPermission.value = it
              if(permissionGranted) {
                 showToast("permission Granted Now request Location")
                  requestLocation()
              }
          }

          //  ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),100)
        }else{

            Constants._isPermission.value = true

            if (!locationManger.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
                    showToast("Network Provider is diabled so request location")
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

                /** Here we are getting current location on this method called (requestLocation) if already location is enabled*/
                Toast.makeText(context, "location Already Enabled", Toast.LENGTH_SHORT).show()

                // TODO : FETCH CURRENT LOCATION HERE
                getLastLocation()


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
                            Toast.makeText(context,e.message.toString(),Toast.LENGTH_SHORT).show()
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

             //TODO : FETCH CURRENT LOCATION HERE
               getLastLocation()

             Toast.makeText(context, "location Enabled", Toast.LENGTH_SHORT).show()
         }else{
             locationStatusOk = false
             showToast("Location should be on \n Please press ok to enable and current location")
             requestLocation()

             /** GETTING PREVIOUS LOCATION HERE IF USER PRESS NO THANKS */
             getLastLocation()
         }

     }

    fun unregisterListener()
    {
        fusedLocationProviderClient.removeLocationUpdates(this)
    }

    fun getLastLocation(){
        try {
            if (checkLocationPermission()) {
            //    fusedLocationProviderClient.requestLocationUpdates(locationRequest, this, Looper.getMainLooper())


                fusedLocationProviderClient.lastLocation?.addOnSuccessListener { location ->

                    if(location != null)
                    {
                        currentlocation = location
                    }
                }
            }
            showToast(" ${currentlocation?.let { getLoc_from_latLng(it)?.get(0)?.subLocality.toString() }}  ${currentlocation?.latitude}" )

            currentlocation?.let { myListener.onLocationChanged(it) }

        }catch (e:Exception){
            showToast("${e.cause} \n ${e.message}" )
        }
    }



    fun getLoc_from_latLng(location : Location) = geocoder.getFromLocation(location.latitude,location.longitude,3)



    fun checkProviders(){
        val hasGps = locationManger.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManger.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun showToast(message:String)
    {
        Toast.makeText(context,message.toString(),Toast.LENGTH_SHORT).show()
    }


    private fun isMockLocationEnabled(): Boolean {
        val isMockLocation: Boolean
        isMockLocation = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val opsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                Objects.requireNonNull(opsManager).checkOp(
                    AppOpsManager.OPSTR_MOCK_LOCATION,
                    Process.myUid(),
                    BuildConfig.APPLICATION_ID
                ) === AppOpsManager.MODE_ALLOWED
            } else {
                Settings.Secure.getString(context.getContentResolver(), "mock_location") != "0"
            }
        } catch (e: Exception) {
            return false
        }
        return isMockLocation
    }

    private fun setMock(provider: String, latitude: Double, longitude: Double) {
        locationManger.addTestProvider(
            provider,
            false,
            false,
            false,
            false,
            false,
            true,
            true,
            0,5
        )
        val newLocation = Location(provider)
        newLocation.latitude = latitude
        newLocation.longitude = longitude
        newLocation.altitude = 3.0
        newLocation.time = System.currentTimeMillis()
        newLocation.speed = 0.01f
        newLocation.bearing = 1f
        newLocation.accuracy = 3f
        newLocation.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            newLocation.bearingAccuracyDegrees = 0.1f
            newLocation.verticalAccuracyMeters = 0.1f
            newLocation.speedAccuracyMetersPerSecond = 0.01f
        }
      locationManger.setTestProviderEnabled(provider, true)
      locationManger.setTestProviderLocation(provider, newLocation)
    }

    fun stopUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(this)
    }

    fun registerListener(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, this, Looper.myLooper())
    }

}


