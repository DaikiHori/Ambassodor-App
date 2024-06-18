package com.ambassador.app

import android.app.Application
import com.ambassador.app.data.AppContainer
import com.ambassador.app.data.AppDataContainer

class AmbassadorApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
