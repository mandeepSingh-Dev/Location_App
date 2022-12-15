package com.example.location_app.di

import android.content.Context
import androidx.room.Room
import com.example.location_app.Database.LocationDatabase
import com.example.location_app.Utils.Constants
import com.example.location_app.preference.DataStorePref
import com.example.location_app.preference.DataStorePrefImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class Moduels {

    @Provides
    @Singleton
    fun provideLocationDatabase(@ApplicationContext context: Context) =  Room.databaseBuilder(context,
        LocationDatabase::class.java,
        Constants.LOCATION_DATABASE).fallbackToDestructiveMigration().build()

    @Provides
    fun provideLocationDaoo(locationDatabase: LocationDatabase) = locationDatabase.getLocation_Dao()

    @Provides
    @Singleton
    fun provideDataStorepref(@ApplicationContext context: Context): DataStorePref {
        return DataStorePrefImpl(context)
    }
}