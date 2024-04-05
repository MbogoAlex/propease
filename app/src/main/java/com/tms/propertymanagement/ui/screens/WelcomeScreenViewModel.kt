package com.tms.propertymanagement.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tms.propertymanagement.network.ApiRepository
import com.tms.propertymanagement.propEaseDataStore.DSRepository
import com.tms.propertymanagement.propEaseDataStore.DSUserModel
import com.tms.propertymanagement.utils.ReusableFunctions
import com.tms.propertymanagement.utils.ReusableFunctions.toLoggedInUserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WelcomeScreenUiState(
    val userDetails: ReusableFunctions.LoggedInUserData = ReusableFunctions.LoggedInUserData(),
    val navigateToNextScreen: Boolean = false
)

class WelcomeScreenViewModel(
    private val apiRepository: ApiRepository,
    private val dsRepository: DSRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _uiState = MutableStateFlow(value = WelcomeScreenUiState())
    val uiState: StateFlow<WelcomeScreenUiState> = _uiState.asStateFlow()

    fun loadUserDetails() {
        viewModelScope.launch {
            dsRepository.dsUserModel.collect() {dsUserModel ->
                _uiState.update {
                    it.copy(
                        userDetails = dsUserModel.toLoggedInUserData()
                    )
                }
            }
        }
    }

    fun saveNewUser() {
        viewModelScope.launch {
            val newUser = dsRepository.saveUserData(
                dsUserModel = DSUserModel(
                    userId = 0,
                    userName = "",
                    phoneNumber = "",
                    email = "",
                    password = "",
                    token = ""
                )
            )
        }
    }

    fun resetNavigationStatus() {
        _uiState.update {
            it.copy(
                navigateToNextScreen = !(_uiState.value.navigateToNextScreen)
            )
        }
    }

    init {
        loadUserDetails()
    }
}