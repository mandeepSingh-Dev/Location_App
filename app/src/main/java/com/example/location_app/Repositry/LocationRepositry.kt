package com.example.location_app.Repositry

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.unit.Constraints
import androidx.core.app.ActivityCompat
import com.example.location_app.Dao.LocationDao
import com.example.location_app.Dao.LocationDao_Impl
import com.example.location_app.Model.LocationEntity
import com.example.location_app.Utils.Constants
import com.example.location_app.Utils.LocationFetcher
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class LocationRepositry @Inject constructor(val locationDao: LocationDao) {

    var currentLocation : Location? = null
    var locationFetcher : LocationFetcher ? =null

    lateinit var  locationCallback : LocationCallback

    lateinit var context:Context
    var fusedLocationProviderClient : FusedLocationProviderClient?=null

     fun initLocationFetcher(context:Context){

         this.context = context


         val locationManager :LocationManager by lazy { context.getSystemService(Context.LOCATION_SERVICE) as LocationManager }
         val locationRequest = LocationRequest.create().setInterval(3000L).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

         fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)


         val isGpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
         val isNetworkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

         CoroutineScope(Dispatchers.Main).launch {
             Constants.ispermission.collect {
              if(it)
              {
                  if(isGpsProvider or isNetworkProvider) {

                      if (ActivityCompat.checkSelfPermission(
                              context,
                              Manifest.permission.ACCESS_FINE_LOCATION
                          ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                              context,
                              Manifest.permission.ACCESS_COARSE_LOCATION
                          ) != PackageManager.PERMISSION_GRANTED
                      ) {
                          // TODO: Consider calling
                          //    ActivityCompat#requestPermissions
                          // here to request the missing permissions, and then overriding
                          //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                          //                                          int[] grantResults)
                          // to handle the case where the user grants the permission. See the documentation
                          // for ActivityCompat#requestPermissions for more details.
                          return@collect
                      }
                      fusedLocationProviderClient?.lastLocation?.addOnSuccessListener {
                          currentLocation = it

                          CoroutineScope(Dispatchers.Main).launch {
                              val ml = System.currentTimeMillis()
                              locationDao.insert(LocationEntity(currentLocation?.longitude!!,currentLocation?.latitude!!, " ${ getLocationInfo(currentLocation!!)?.get(0)?.subLocality } \n ${ getLocationInfo(currentLocation!!)?.get(0)?.locality } \n ${ getLocationInfo(currentLocation!!)?.get(0)?.subAdminArea } \n ${ getLocationInfo(currentLocation!!)?.get(0)?.adminArea } "))
                         Toast.makeText(context,"inserted",Toast.LENGTH_SHORT).show()
                          }
                      }?.addOnFailureListener {
                          println("ggg:${it.message}")
                      }

                  }
                  else{
                      CoroutineScope(Dispatchers.Main).launch {
                          Toast.makeText(context,"Please Enable Location",Toast.LENGTH_SHORT).show()
                      }
                  }

                  /*     fusedLocationProviderClient?.lastLocation?.addOnSuccessListener {
                           currentLocation = it

                           CoroutineScope(Dispatchers.Main).launch {
                              // locationDao.insert(LocationEntity(currentLocation?.longitude!!,currentLocation?.latitude!!," "))
                           }
                       }*/



                  var isInserted = false
                  locationCallback  = object : LocationCallback(){
                      override fun onLocationResult(location: LocationResult?) {
                          val dfhdf =  false
                          super.onLocationResult(location)
                          currentLocation = location?.lastLocation
                          Toast.makeText(context,currentLocation?.latitude.toString()+ "Locationcallback ",Toast.LENGTH_LONG).show()


                          CoroutineScope(Dispatchers.Main).launch {
                              locationDao.insert(LocationEntity(currentLocation?.longitude!!,currentLocation?.latitude!!," ${ getLocationInfo(currentLocation!!)?.get(0)?.subLocality } \n ${ getLocationInfo(currentLocation!!)?.get(0)?.locality } \n ${ getLocationInfo(currentLocation!!)?.get(0)?.subAdminArea } \n ${ getLocationInfo(currentLocation!!)?.get(0)?.adminArea } ")).let {
                                  isInserted = true
                                  fusedLocationProviderClient?.removeLocationUpdates(locationCallback)

                              }
                          }

                      }

                      override fun onLocationAvailability(locationAvailability : LocationAvailability?) {
                          super.onLocationAvailability(locationAvailability)
                          if(locationAvailability?.isLocationAvailable == true && isInserted )
                          {
                             //  fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
                          }

                      }
                  }

                  Log.d("TAG", "initLocationFetcher: $fusedLocationProviderClient ")
                  fusedLocationProviderClient?.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())

              }
                 else{
                  Toast.makeText(context,"StateFlowIspermission  "+it.toString(),Toast.LENGTH_SHORT).show()

              }
             }
         }





     }

    fun unregisterLocationUpdates(){
        if(fusedLocationProviderClient != null)
        {
            fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
        }
    }


    fun convertMilliscons(millisceonds:Long) : String{

         val  minutes = (millisceonds / 1000) / 60;
        //val minutes = TimeUnit.MILLISECONDS.toMinutes(millisceonds)

         val  seconds = (millisceonds / 1000);
        //val seconds = TimeUnit.MILLISECONDS.toSeconds(millisceonds)

        return " ${minutes.toString()} : ${seconds.toString()}"
    }

    fun getLocationInfo(location : Location) = Geocoder(context).getFromLocation(location.latitude,location.longitude,3)

}