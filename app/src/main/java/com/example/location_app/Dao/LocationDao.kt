package com.example.location_app.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.location_app.Model.LocationEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mLocation: LocationEntity)

    @Query( "SELECT * FROM locationTable")
    fun getLocations(): Flow<List<LocationEntity>>

}