package com.tms.propertymanagement.ui.screens.appContentPages

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tms.propertymanagement.apiModel.PropertyData
import com.tms.propertymanagement.apiModel.PropertyLocation
import com.tms.propertymanagement.apiModel.PropertyOwner
import com.tms.propertymanagement.network.ApiRepository
import com.tms.propertymanagement.propEaseDataStore.DSRepository
import com.tms.propertymanagement.utils.ReusableFunctions
import com.tms.propertymanagement.utils.ReusableFunctions.toLoggedInUserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SpecificPropertyData(
    val property: PropertyData
)

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
    availableDate = "",
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

data class ListingDetailsScreenUiState(
    val property: PropertyData = propertyData,
    val fetchingStatus: PropertyFetchingStatus = PropertyFetchingStatus.INITIAL,
    val userDetails: ReusableFunctions.LoggedInUserData = ReusableFunctions.LoggedInUserData()
)

class ListingDetailsScreenViewModel(
    private val apiRepository: ApiRepository,
    private val dsRepository: DSRepository,
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {
    private val _uiState = MutableStateFlow(value = ListingDetailsScreenUiState())
    val uiState: StateFlow<ListingDetailsScreenUiState> = _uiState.asStateFlow()

    private val propertyId: String? = savedStateHandle[ListingDetailsDestination.propertyId]
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

    fun fetchProperty() {
        _uiState.update {
            it.copy(
                fetchingStatus = PropertyFetchingStatus.LOADING
            )
        }
        viewModelScope.launch {
            try {
                Log.i("FETCHING_WITH_TOKEN", _uiState.value.userDetails.token)
                val response = apiRepository.fetchSpecificProperty(_uiState.value.userDetails.token, propertyId = propertyId!!)
                if(response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            property = response.body()?.data?.property!!,
                            fetchingStatus = PropertyFetchingStatus.SUCCESS
                        )
                    }
                    Log.i("PROPERTIES_FETCHED_IMAGES_ARE", _uiState.value.property.images.toString())
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

    init {
        loadUserDetails()
        fetchProperty()
    }

}