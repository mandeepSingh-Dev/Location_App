package com.example.location_app

import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.example.location_app.Base.BaseActivity
import com.example.location_app.Dao.LocationDao
import com.example.location_app.Database.LocationDbSingleton
import com.example.location_app.Repositry.LocationRepositry
import com.example.location_app.Utils.Constants
import com.example.location_app.Utils.LocationFetcher
import com.example.location_app.Workers.LocationWorker
import com.example.location_app.adapters.LocationAdapter
import com.example.location_app.databinding.ActivityMain2Binding
import com.example.location_app.preference.DataStorePref
import com.example.location_app.preference.DataStorePrefImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity2 : BaseActivity(), LocationFetcher.MyListener {

    lateinit var binding : ActivityMain2Binding

    @Inject
    lateinit var locationDao:LocationDao
    val locationAdapter = LocationAdapter(this)

    @Inject
    lateinit var dataStorePrefImpl: DataStorePref


    lateinit var locationFetcher: LocationFetcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)



        setupLocationFetcher()

      lifecycleScope.launch {
         // setUpWorkManager()

          dataStorePrefImpl.getPreference(Constants.WORKMANAGER_EXECUTED,false).collect{
           if(it == false)
           {
               setUpWorkManager()
           }else{}
          }
      }





        binding.locationRecyclerView.adapter = locationAdapter


        val addDataObserver = object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.locationRecyclerView.layoutManager?.scrollToPosition(positionStart)

            }
        }
        locationAdapter.registerAdapterDataObserver(addDataObserver)

        lifecycleScope.launch {
            locationDao.getLocations().collect {
                withContext(Dispatchers.Main) {
                    locationAdapter.submitList(it)
                }
            }
        }

    }

   suspend fun setUpWorkManager(){



        val periodicWorkRequest = PeriodicWorkRequest.Builder(LocationWorker::class.java,15,TimeUnit.MINUTES)
            .addTag("MyLocationWorker")
            .build()

        val workmanagerOperation =  WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork("MyLocationWorker",ExistingPeriodicWorkPolicy.REPLACE,periodicWorkRequest)
            .let {
                dataStorePrefImpl.putPreference(Constants.WORKMANAGER_EXECUTED,true)
            }
    }

    fun setupLocationFetcher(){
        locationFetcher = LocationFetcher(context = this, myListtener = this)
        locationFetcher.requestLocationPermission()
    }



    override fun onLocationChanged(location: Location) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        locationFetcher.onActivityResult(requestCode,resultCode,data)
        super.onActivityResult(requestCode, resultCode, data)

    }

}