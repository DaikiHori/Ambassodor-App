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
                ambassadorApplication().container.codesRepository
            )
        }

        initializer {
            HomeViewModel(ambassadorApplication().container.eventsRepository)
        }
    }
}

fun CreationExtras.ambassadorApplication(): AmbassadorApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as AmbassadorApplication)
