package com.example.location_app.preference

import android.app.Application
import android.content.Context
import androidx.compose.ui.unit.Constraints
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.location_app.Utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException


private val Context.dataStore by preferencesDataStore(name = Constants.APP_DATASTORE)

class DataStorePrefImpl( context: Context) : DataStorePref {

    val dataStore = context.dataStore

    override suspend fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return  dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val result = preferences[key]?: defaultValue
            result
        }    }

    override suspend fun <T> putPreference(key: Preferences.Key<T>, value: T) {
    dataStore.edit {
        it[key] = value
    }
    }

    override suspend fun <T> removePreference(key: Preferences.Key<T>) {
      dataStore.edit {
          it.remove(key)
      }
    }

    override suspend fun clearAllPreference() {
      dataStore.edit {
          it.clear()
      }
    }
}