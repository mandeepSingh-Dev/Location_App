package com.example.location_app.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "locationTable"/*, indices = [Index(value = ["longitude", "latitude"], unique = true)]*/)
data class LocationEntity (

    @ColumnInfo (name = "longitude")
    var longitude:Double,
    @ColumnInfo (name = "latitude")
    var latitude:Double,
    @ColumnInfo (name = "subLocality")
    var subLocality:String
    )
{
    @PrimaryKey(autoGenerate = true)
    var id = 0

}
