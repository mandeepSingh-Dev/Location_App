package com.example.location_app.Workers

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.location_app.Repositry.LocationRepositry
import com.example.location_app.Utils.LocationFetcher
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@HiltWorker
class LocationWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted params: WorkerParameters,
    val locationRepositry: LocationRepositry) : CoroutineWorker( appContext, params ) {

    override suspend fun doWork(): Result {
        try {
                locationRepositry.initLocationFetcher(appContext)
               // locationRepositry.unregisterListener()

            return  Result.success()
        } catch (e: Exception) {
            Log.d("knkn",e.message.toString())

        return Result.retry()
        }
    }
}