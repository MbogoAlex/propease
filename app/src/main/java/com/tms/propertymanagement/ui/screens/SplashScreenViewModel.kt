package com.propertymanagement.tms.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanagement.tms.propEaseDataStore.DSRepository
import com.propertymanagement.tms.propEaseDataStore.DSUserModel
import com.propertymanagement.tms.utils.ReusableFunctions
import com.propertymanagement.tms.utils.ReusableFunctions.toLoggedInUserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AUTHENTICATION_STATUS {
    INITIAL,
    LOADING,
    SUCCESS,
    FAILURE
}
data class SplashScreenUiState(
    val authenticationStatus: AUTHENTICATION_STATUS = AUTHENTICATION_STATUS.INITIAL,
    val loggedInUserData: ReusableFunctions.LoggedInUserData = ReusableFunctions.LoggedInUserData()
)
class SplashScreenViewModel(
    private val dsRepository: DSRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(value = SplashScreenUiState())
    val uiState: StateFlow<SplashScreenUiState> = _uiState.asStateFlow()

    fun fetchUserDetails() {
        _uiState.update {
            it.copy(
                authenticationStatus = AUTHENTICATION_STATUS.LOADING
            )
        }
        viewModelScope.launch {
            dsRepository.dsUserModel.collect() { dsUserModel ->
                _uiState.update {
                    it.copy(
                        authenticationStatus = AUTHENTICATION_STATUS.SUCCESS,
                        loggedInUserData = dsUserModel.toLoggedInUserData()
                    )
                }
            }
        }
    }

    fun resetAuthenticationStatus() {
        _uiState.update {
            it.copy(
                authenticationStatus = AUTHENTICATION_STATUS.INITIAL
            )
        }
    }

    init {
        fetchUserDetails()
    }
}