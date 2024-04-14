package com.propertymanagement.tms.ui.screens.propertyAdvertisementPages

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanagement.tms.apiModel.PropertyData
import com.propertymanagement.tms.apiModel.PropertyLocation
import com.propertymanagement.tms.apiModel.PropertyOwner
import com.propertymanagement.tms.network.ApiRepository
import com.propertymanagement.tms.propEaseDataStore.DSRepository
import com.propertymanagement.tms.utils.ReusableFunctions
import com.propertymanagement.tms.utils.ReusableFunctions.toLoggedInUserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val propertyOwner: PropertyOwner = PropertyOwner(
    userId = 0,
    email = "",
    phoneNumber = "",
    profilePic = "",
    fname = "",
    lname = "",
)

val propertyLocation = PropertyLocation(
    county = "",
    address = "",
    latitude = 0.0,
    longitude = 0.0
)

val propertyData = PropertyData(
    user = propertyOwner,
    propertyId = 0,
    title = "",
    description = "",
    category = "",
    rooms = 0,
    price = 0.0,
    postedDate = "",
    features = emptyList(),
    location = propertyLocation,
    images = emptyList()
)

enum class PropertyFetchingStatus{
    INITIAL,
    LOADING,
    SUCCESS,
    FAIL,
}

enum class DeletingPropertyStatus{
    INITIAL,
    LOADING,
    SUCCESS,
    FAIL,
}

data class UserLivePropertyDetailsScreenUiState(
    val property: PropertyData = propertyData,
    val fetchingStatus: PropertyFetchingStatus = PropertyFetchingStatus.INITIAL,
    val deletingPropertyStatus: DeletingPropertyStatus = DeletingPropertyStatus.INITIAL,
    val userDetails: ReusableFunctions.LoggedInUserData = ReusableFunctions.LoggedInUserData()
)

class UserLivePropertyDetailsScreenViewModel(
    private val apiRepository: ApiRepository,
    private val dsRepository: DSRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow(value = UserLivePropertyDetailsScreenUiState())
    val uiState: StateFlow<UserLivePropertyDetailsScreenUiState> = _uiState.asStateFlow()

    private val propertyId: String? = savedStateHandle[UserLivePropertyDetailsScreenDestination.propertyId]
    fun fetchUserDetails() {
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

    fun fetchProperty() {
        _uiState.update {
            it.copy(
                fetchingStatus = PropertyFetchingStatus.LOADING
            )
        }
        viewModelScope.launch {
            try {
                Log.i("FETCHING_WITH_TOKEN", _uiState.value.userDetails.token)
                val response = apiRepository.fetchSpecificProperty(propertyId = propertyId!!)
                if(response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            property = response.body()?.data?.property!!,
                            fetchingStatus = PropertyFetchingStatus.SUCCESS
                        )
                    }
                    Log.i("LIVE_PROPERTIES_FETCHED", _uiState.value.property.toString())
                } else {
                    _uiState.update {
                        it.copy(
                            fetchingStatus = PropertyFetchingStatus.FAIL
                        )
                    }
                    Log.e("FAILED_TO_FETCH_PROPERTY", response.toString())
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        fetchingStatus = PropertyFetchingStatus.FAIL
                    )
                }
                Log.e("FAILED_TO_FETCH_PROPERTY_EXCEPTION", e.message.toString())
            }
        }
    }

    fun deleteProperty() {
        _uiState.update {
            it.copy(
                deletingPropertyStatus = DeletingPropertyStatus.LOADING
            )
        }
        viewModelScope.launch {
            try {
                val response = apiRepository.deleteProperty(
                    token = _uiState.value.userDetails.token,
                    propertyId = propertyId!!.toInt()
                )
                if(response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            deletingPropertyStatus = DeletingPropertyStatus.SUCCESS
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            deletingPropertyStatus = DeletingPropertyStatus.FAIL
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        deletingPropertyStatus = DeletingPropertyStatus.FAIL
                    )
                }
            }
        }
    }

    fun resetDeletingStatus() {
        _uiState.update {
            it.copy(
                deletingPropertyStatus = DeletingPropertyStatus.INITIAL
            )
        }
    }

    init {
        fetchUserDetails()
        fetchProperty()
    }
}