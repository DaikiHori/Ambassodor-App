package com.cambassador.app.ui.users

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cambassador.app.data.User
import com.cambassador.app.data.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UsersViewModel(
    savedStateHandle: SavedStateHandle,
    private val usersRepository: UsersRepository
): ViewModel() {
    private var userUiState = MutableStateFlow<List<User>?>(null)
    val displayUsers: StateFlow<List<User>?> = userUiState
    init {
        viewModelScope.launch {
            usersRepository.getAllUsersStream()
                .collect{data -> userUiState.value = data}
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}