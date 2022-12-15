package com.example.location_app.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.location_app.Dao.LocationDao
import com.example.location_app.Model.LocationEntity

@Database( entities = arrayOf(LocationEntity::class), version = 2)
abstract class LocationDatabase : RoomDatabase() {

    abstract fun getLocation_Dao() : LocationDao
}
