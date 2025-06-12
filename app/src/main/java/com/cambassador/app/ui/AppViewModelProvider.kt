package com.cambassador.app.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.cambassador.app.AmbassadorApplication
import com.cambassador.app.ui.home.HomeViewModel
import com.cambassador.app.ui.event.EventDetailsViewModel
import com.cambassador.app.ui.event.EventEditViewModel
import com.cambassador.app.ui.event.EventEntryViewModel
import com.cambassador.app.ui.codes.CodesViewModel
import com.cambassador.app.ui.codes_in_event.CodesDetailsViewModel
import com.cambassador.app.ui.users.UsersViewModel
import com.cambassador.app.ui.users.UserEntryViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            EventEditViewModel(
                this.createSavedStateHandle(),
                ambassadorApplication().container.eventsRepository
            )
        }

        initializer {
            EventEntryViewModel(
                ambassadorApplication().container.eventsRepository,
                ambassadorApplication().container.codesRepository
            )
        }

        initializer {
            EventDetailsViewModel(
                this.createSavedStateHandle(),
                ambassadorApplication().container.eventsRepository
            )
        }

        initializer {
            CodesViewModel(
                this.createSavedStateHandle(),
                ambassadorApplication().container.eventsRepository,
                ambassadorApplication().container.codesRepository,
                ambassadorApplication().container.usersRepository
            )
        }

        initializer {
            CodesDetailsViewModel(
                this.createSavedStateHandle(),
                ambassadorApplication().container.eventsRepository,
                ambassadorApplication().container.codesRepository,
                ambassadorApplication().container.usersRepository
            )
        }

        initializer {
            HomeViewModel(ambassadorApplication().container.eventsRepository)
        }

        initializer {
            UsersViewModel(ambassadorApplication().container.usersRepository)
        }

        initializer {
            UserEntryViewModel(
                this.createSavedStateHandle(),
                ambassadorApplication().container.usersRepository
            )
        }
    }
}

fun CreationExtras.ambassadorApplication(): AmbassadorApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as AmbassadorApplication)
