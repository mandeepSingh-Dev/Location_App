package com.example.location_app.Database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.location_app.Dao.LocationDao
import com.example.location_app.Utils.Constants

object LocationDbSingleton {

    fun provideLocationDao(context:Context) : LocationDao {
        val locationdb = Room.databaseBuilder(context,LocationDatabase::class.java,Constants.LOCATION_DATABASE).build()

        return locationdb.getLocation_Dao()

    }
}