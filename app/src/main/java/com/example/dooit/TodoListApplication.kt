package com.example.dooit

import android.app.Application
import com.example.dooit.data.AppConainer
import com.example.dooit.data.DefaultContainer

class TodoListApplication: Application() {
    lateinit var container: AppConainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultContainer(this)
    }
}