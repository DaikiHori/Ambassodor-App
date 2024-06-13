package com.ambassador.app.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ambassador.app.AmbassadorApplication
import com.ambassador.app.ui.home.HomeViewModel
import com.ambassador.app.ui.event.EventDetailsViewModel
import com.ambassador.app.ui.event.EventEditViewModel
import com.ambassador.app.ui.event.EventEntryViewModel
import com.ambassador.app.ui.codes.CodesViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for EventEditViewModel
        initializer {
            EventEditViewModel(
                this.createSavedStateHandle(),
                ambassadorApplication().container.eventsRepository
            )
        }
        // Initializer for EventEntryViewModel
        initializer {
            EventEntryViewModel(
                ambassadorApplication().container.eventsRepository,
                ambassadorApplication().container.codesRepository
            )
        }

        // Initializer for EventDetailsViewModel
        initializer {
            EventDetailsViewModel(
                this.createSavedStateHandle(),
                ambassadorApplication().container.eventsRepository
            )
        }

        // Initializer for CodesViewModel
        initializer {
            CodesViewModel(
                this.createSavedStateHandle(),
                ambassadorApplication().container.eventsRepository,
                ambassadorApplication().container.codesRepository
            )
        }

        // Initializer for HomeViewModel
        initializer {
            HomeViewModel(ambassadorApplication().container.eventsRepository)
        }
    }
}

fun CreationExtras.ambassadorApplication(): AmbassadorApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as AmbassadorApplication)
