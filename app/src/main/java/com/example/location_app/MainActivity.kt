package com.example.location_app

import android.content.Intent
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

class MainActivity : BaseActivity() {

    lateinit var locationFetcher:LocationFetcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationFetcher = LocationFetcher(context = this, FusedLocationProviderClient(this))

        locationFetcher.init()

    }

    override fun onDestroy() {
        super.onDestroy()
        locationFetcher.unregisterListener()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        locationFetcher.onActivityResult(requestCode,resultCode,data)
        super.onActivityResult(requestCode, resultCode, data)

    }
}

