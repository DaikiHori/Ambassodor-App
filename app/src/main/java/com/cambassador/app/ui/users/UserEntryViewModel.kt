package com.cambassador.app.ui.users

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.cambassador.app.data.User
import com.cambassador.app.data.UsersRepository

class UserEntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val usersRepository: UsersRepository
): ViewModel() {
    var userUiState by mutableStateOf(UserUiState())

    fun updateUiState(userDetails: UserDetails){
        userUiState = UserUiState(userDetails = userDetails, isEntryValid = validateInput(userDetails))
    }

    suspend fun saveUser(){
        if(validateInput()){
            try{
                val names = userUiState.userDetails.name
                    .replace("\"","")
                    .replace("\r",",").replace("\n",",")
                    .split(",")
                for(n in names){
                    usersRepository.insertUser(User(name = n))
                }
            }catch (e: Throwable){
                throw e
            }
        }
    }

    private fun validateInput(users: UserDetails = userUiState.userDetails): Boolean{
        return users.name.isNotBlank()
    }
}

data class UserUiState(
    val userDetails : UserDetails = UserDetails(id = 0, name = ""),
    val isEntryValid: Boolean = false
)

data class UserDetails(
    val id: Int = 0,
    val name: String = ""
)