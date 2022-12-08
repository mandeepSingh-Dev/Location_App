package com.example.location_app

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.location_app.Base.BaseActivity
import com.example.location_app.Dao.LocationDao
import com.example.location_app.Database.LocationDbSingleton
import com.example.location_app.Model.LocationEntity
import com.example.location_app.Utils.LocationFetcher
import com.example.location_app.adapters.LocationAdapter
import com.example.location_app.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : BaseActivity(), LocationFetcher.MyListener {

    lateinit var locationFetcher: LocationFetcher

    lateinit var binding : ActivityMainBinding
    lateinit var locationDao: LocationDao
    val locationAdapter = LocationAdapter(this)

    var isStopped = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationDao = LocationDbSingleton.provideLocationDao(this)

        locationFetcher = LocationFetcher(context = this,this)
        locationFetcher.init()


       val linearLayoutManager = LinearLayoutManager(this)
        binding.locationRecyclerView.layoutManager = linearLayoutManager
        binding.locationRecyclerView.adapter = locationAdapter


        val addDataObserver = object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                linearLayoutManager.scrollToPositionWithOffset(positionStart, 0)

            }
        }
        locationAdapter.registerAdapterDataObserver(addDataObserver)

        lifecycleScope.launch {
            locationDao.getLocations().collect {
                withContext(Dispatchers.Main) {
                    locationAdapter.submitList(it)
                }

                it.forEach {
                    Log.d("dfodfmdfd",it.latitude.toString())
                }
            }
        }

        binding.stopButton.setOnClickListener {
            isStopped = true
            locationFetcher.unregisterListener()
        }

        binding.startButton.setOnClickListener {
            if(isStopped) {
                locationFetcher.registerListener()
            }
            isStopped = false
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        locationFetcher.unregisterListener()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        locationFetcher.onActivityResult(requestCode,resultCode,data)
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onLocationChanged(location: Location) {


        if(location.latitude != null) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.locationTextView.text = locationFetcher.currentlocation?.latitude.toString()
                locationDao.insert(LocationEntity(location.longitude, location.latitude, locationFetcher.getLoc_from_latLng(location)?.get(0)?.subLocality.toString()))
            }
        }else{
            binding.locationTextView.text = "null"
        }



        }
}

