package com.example.location_app.Utils

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object Constants {

    const val LOCATION_DATABASE = "location_db"
    const val APP_DATASTORE = "com.locationapp"

    val WORKMANAGER_EXECUTED = booleanPreferencesKey("Workmanager-executed")

    val _isPermission = MutableStateFlow(false)
    val ispermission: StateFlow<Boolean> = _isPermission
}