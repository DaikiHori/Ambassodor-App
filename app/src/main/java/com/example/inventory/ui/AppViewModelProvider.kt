package com.example.inventory.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.inventory.InventoryApplication
import com.example.inventory.ui.home.HomeViewModel
import com.example.inventory.ui.event.EventDetailsViewModel
import com.example.inventory.ui.event.EventEditViewModel
import com.example.inventory.ui.event.EventEntryViewModel
import com.example.inventory.ui.codes.CodesViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for EventEditViewModel
        initializer {
            EventEditViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.eventsRepository
            )
        }
        // Initializer for EventEntryViewModel
        initializer {
            EventEntryViewModel(
                inventoryApplication().container.eventsRepository,
                inventoryApplication().container.codesRepository
            )
        }

        // Initializer for EventDetailsViewModel
        initializer {
            EventDetailsViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.eventsRepository
            )
        }

        // Initializer for CodesViewModel
        initializer {
            CodesViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.eventsRepository,
                inventoryApplication().container.codesRepository
            )
        }

        // Initializer for HomeViewModel
        initializer {
            HomeViewModel(inventoryApplication().container.eventsRepository)
        }
    }
}

fun CreationExtras.inventoryApplication(): InventoryApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as InventoryApplication)
