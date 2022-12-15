/*
package com.example.location_app

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.location_app.Dao.LocationDao
import com.example.location_app.Database.LocationDbSingleton
import com.example.location_app.Model.LocationEntity
import com.example.location_app.Utils.LocationFetcher
import com.example.location_app.databinding.ActivityMainBinding
import com.example.location_app.ui.theme.Location_AppTheme
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TestActivity : ComponentActivity(), LocationFetcher.MyListener {

    lateinit var locationFetcher: LocationFetcher

    lateinit var binding : ActivityMainBinding
    lateinit var locationDao: LocationDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Location_AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }

        locationDao = LocationDbSingleton.provideLocationDao(applicationContext)

        locationFetcher = LocationFetcher(context = this, FusedLocationProviderClient(this),this)
        locationFetcher.init()


        CoroutineScope(Dispatchers.IO).launch {
            locationDao.getLocations().collect {
                it.forEach {
                    Log.d("dfodfmdfd",it.latitude.toString())
                }
            }
        }



    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Location_AppTheme {
        Greeting("Android")
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
        binding.locationTextView.text = locationFetcher.currentlocation?.latitude.toString()
    }else{
        binding.locationTextView.text = "null"
    }
    CoroutineScope(Dispatchers.IO).launch {
        locationDao.insert(LocationEntity(location.longitude, location.latitude, "Sublocality"))
    }
}*/
