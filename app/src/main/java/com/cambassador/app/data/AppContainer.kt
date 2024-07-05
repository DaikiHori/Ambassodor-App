package com.cambassador.app.data

import android.content.Context

interface AppContainer {
    val eventsRepository: EventsRepository
    val codesRepository: CodesRepository
    val usersRepository: UsersRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val eventsRepository: EventsRepository by lazy {
        OfflineEventsRepository(AmbassadorDatabase.getDatabase(context).eventDao())
    }

    override val codesRepository: CodesRepository by lazy {
        OfflineCodesRepository(AmbassadorDatabase.getDatabase(context).codeDao())
    }

    override val usersRepository: UsersRepository by lazy {
        OfflineUsersRepository(AmbassadorDatabase.getDatabase(context).userDao())
    }
}
