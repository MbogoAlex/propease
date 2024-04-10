package com.propertymanagement.tms.ui.screens.propertyAdvertisementPages

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanagement.tms.apiModel.PropertyData
import com.propertymanagement.tms.network.ApiRepository
import com.propertymanagement.tms.propEaseDataStore.DSRepository
import com.propertymanagement.tms.utils.ReusableFunctions
import com.propertymanagement.tms.utils.ReusableFunctions.toLoggedInUserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class FetchingStatus {
    INITIAL,
    LOADING,
    SUCCESS,
    FAILURE
}

data class UserLivePropertiesScreenUiState(
    val properties: List<PropertyData> = emptyList(),
    val fetchingStatus: FetchingStatus = FetchingStatus.INITIAL,
    val userDetails: ReusableFunctions.LoggedInUserData = ReusableFunctions.LoggedInUserData(),
    val showPropertyUploadScreen: Boolean = false

)
class UserLivePropertiesScreenViewModel(
    private val apiRepository: ApiRepository,
    private val dsRepository: DSRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(value = UserLivePropertiesScreenUiState())
    val uiState: StateFlow<UserLivePropertiesScreenUiState> = _uiState.asStateFlow()
    fun fetchUserDetails() {
        viewModelScope.launch {
            dsRepository.dsUserModel.collect() { dsUserModel ->
                _uiState.update {
                    it.copy(
                        userDetails = dsUserModel.toLoggedInUserData()
                    )
                }
            }
        }
    }

    fun fetchUserProperties() {
        _uiState.update {
            it.copy(
                fetchingStatus = FetchingStatus.LOADING
            )
        }
        viewModelScope.launch {
            try {
                val response = apiRepository.fetchUserProperties(_uiState.value.userDetails.token, _uiState.value.userDetails.userId!!)
                if(response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            properties = response.body()?.data?.properties!!,
                            fetchingStatus = FetchingStatus.SUCCESS
                        )
                    }
                    Log.i("PROPERTIES_FETCHED", response.toString())
                } else {
                    _uiState.update {
                        it.copy(
                            fetchingStatus = FetchingStatus.FAILURE
                        )
                    }
                    Log.e("FAILED_TO_FETCH_USER_PROPERTIES", response.toString())
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        fetchingStatus = FetchingStatus.FAILURE
                    )
                }
                Log.e("FAILED_TO_FETCH_USER_PROPERTIES_EXCEPTION", e.message.toString())
            }

        }
    }

    fun switchToAndFromPropertyUploadScreen() {
        _uiState.update {
            it.copy(
                showPropertyUploadScreen = !(_uiState.value.showPropertyUploadScreen)
            )
        }
    }

    init {
        fetchUserDetails()
        fetchUserProperties()
    }
}