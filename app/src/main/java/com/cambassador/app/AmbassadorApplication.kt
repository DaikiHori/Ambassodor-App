package com.cambassador.app

import android.app.Application
import com.cambassador.app.data.AppContainer
import com.cambassador.app.data.AppDataContainer

class AmbassadorApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
