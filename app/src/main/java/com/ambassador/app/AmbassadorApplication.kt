package com.ambassador.app

import android.app.Application
import com.ambassador.app.data.AppContainer
import com.ambassador.app.data.AppDataContainer

class AmbassadorApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
