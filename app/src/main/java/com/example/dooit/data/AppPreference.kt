package com.example.dooit.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class AppPreference(
    private val dataStore: DataStore<Preferences>
) {
    val firstTime: Flow<Boolean> = dataStore.data.catch {
        if(it is IOException){
            emit(emptyPreferences())
        } else{
            throw it
        }
    }.map {preferences->
        preferences[FIRST_TIME] ?: false
    }
    private companion object{
        val FIRST_TIME = booleanPreferencesKey("first_time")
    }
    suspend fun updateStatus() {
        dataStore.edit {preferences ->
            preferences[FIRST_TIME] = true
        }
    }
}