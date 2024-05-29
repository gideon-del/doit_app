package com.example.dooit

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.dooit.data.AppConainer
import com.example.dooit.data.AppPreference
import com.example.dooit.data.DefaultContainer
private val APP_STATUS = "app_status"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = APP_STATUS)
class TodoListApplication: Application() {
    lateinit var container: AppConainer
    lateinit var appStatus: AppPreference

    override fun onCreate() {

        super.onCreate()

        container = DefaultContainer(this)
        appStatus = AppPreference(dataStore)
    }
}