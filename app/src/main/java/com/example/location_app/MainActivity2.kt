package com.example.location_app

import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.work.*
import com.example.location_app.Base.BaseActivity
import com.example.location_app.Dao.LocationDao
import com.example.location_app.Database.LocationDbSingleton
import com.example.location_app.Repositry.LocationRepositry
import com.example.location_app.Utils.LocationFetcher
import com.example.location_app.Workers.LocationWorker
import com.example.location_app.databinding.ActivityMain2Binding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity2 : BaseActivity(), LocationFetcher.MyListener {

    lateinit var binding : ActivityMain2Binding

    @Inject
    lateinit var locationDao:LocationDao

    lateinit var locationFetcher: LocationFetcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)



        setupLocationFetcher()
        setUpWorkManager()




        CoroutineScope(Dispatchers.Main).launch {
            locationDao.getLocations().collect{
                it.forEach {
                    binding.tetstd.append(it.latitude.toString()+"\n")
                }
            }
        }
    }

    fun setUpWorkManager(){
        val periodicWorkRequest = PeriodicWorkRequest.Builder(LocationWorker::class.java,15,TimeUnit.MINUTES)
            .addTag("MyLocationWorker")
            .build()

        val workmanagerOperation =  WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork("MyLocationWorker",ExistingPeriodicWorkPolicy.REPLACE,periodicWorkRequest)

        workmanagerOperation.state?.observe(this, Observer {
            //  Toast.makeText(this,it.toString(),Toast.LENGTH_SHORT).show()
        })
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