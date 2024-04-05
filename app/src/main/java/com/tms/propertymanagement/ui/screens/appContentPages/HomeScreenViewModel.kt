package com.tms.propertymanagement.ui.screens.appContentPages

import HomeScreenDestination
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tms.propertymanagement.network.ApiRepository
import com.tms.propertymanagement.propEaseDataStore.DSRepository
import com.tms.propertymanagement.utils.ReusableFunctions
import com.tms.propertymanagement.utils.ReusableFunctions.toLoggedInUserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class HomeScreenUiState(
    val userDetails: ReusableFunctions.LoggedInUserData = ReusableFunctions.LoggedInUserData(),
    val childScreen: String = ""
)
class HomeScreenViewModel(
    private val apiRepository: ApiRepository,
    private val dsRepository: DSRepository,
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {
    private val _uiState = MutableStateFlow(value = HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    private val childScreen: String? = savedStateHandle[HomeScreenDestination.childScreen]
    fun loadStartUpDetails() {
        viewModelScope.launch {
            dsRepository.dsUserModel.collect() { dsUserModel ->
                _uiState.update {
                    it.copy(
                        userDetails = dsUserModel.toLoggedInUserData(),
                        childScreen = childScreen.takeIf { childScreen != null } ?: ""
                    )
                }
            }
        }
    }

    fun resetChildScreen() {
        _uiState.update {
            it.copy(
                childScreen = ""
            )
        }
    }

    init {
        loadStartUpDetails()
    }
}