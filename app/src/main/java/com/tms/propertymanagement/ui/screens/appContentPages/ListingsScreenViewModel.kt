package com.tms.propertymanagement.ui.screens.appContentPages

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tms.propertymanagement.apiModel.Category
import com.tms.propertymanagement.apiModel.PropertyData
import com.tms.propertymanagement.network.ApiRepository
import com.tms.propertymanagement.propEaseDataStore.DSRepository
import com.tms.propertymanagement.utils.ReusableFunctions
import com.tms.propertymanagement.utils.ReusableFunctions.toLoggedInUserData
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

data class ListingsScreenUiState(
    val properties: List<PropertyData> = emptyList(),
    val categories: List<Category> = emptyList(),
    val fetchingStatus: FetchingStatus = FetchingStatus.INITIAL,
//    val categoryId: Int = 1,
    val userDetails: ReusableFunctions.LoggedInUserData = ReusableFunctions.LoggedInUserData(),
    val numberOfRoomsSelected: String = "",
    val categoryIdSelected: String = "",
    val categoryNameSelected: String = "",
    val location: String = ""
)
class ListingsScreenViewModel(
    private val apiRepository: ApiRepository,
    private val dsRepository: DSRepository,
): ViewModel() {
    private val _uiState = MutableStateFlow(value = ListingsScreenUiState())
    val uiState: StateFlow<ListingsScreenUiState> = _uiState.asStateFlow()

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

    fun fetchCategories(token: String) {
        _uiState.update {
            it.copy(
                fetchingStatus = FetchingStatus.LOADING
            )
        }
        viewModelScope.launch {
            try {
                val response = apiRepository.fetchCategories(token)
                if(response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            categories = response.body()?.data?.categories!!,
                            categoryNameSelected = response.body()?.data?.categories!![0].name,
                            fetchingStatus = FetchingStatus.SUCCESS
                        )
                    }
                    fetchProperties(
                        token = _uiState.value.userDetails.token,
                        location = null,
                        rooms = null,
                        categoryId = response.body()?.data?.categories!![0].id.toString()
                    )
                    Log.i("CATEGORIES_FETCHED", "${response.body()?.data?.categories}, 1st Category: ${response.body()?.data?.categories!![0]}")
                } else {
                    _uiState.update {
                        it.copy(
                            fetchingStatus = FetchingStatus.FAILURE
                        )
                    }
                    Log.e("CATEGORIES_NOT_FETCHED", response.body().toString())
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        fetchingStatus = FetchingStatus.FAILURE
                    )
                }
                Log.e("CATEGORIES_NOT_FETCHED_EXCEPTION", e.message.toString())
            }
        }
    }

    fun fetchProperties(token: String, location: String?, rooms: String?, categoryId: String?) {
        viewModelScope.launch {
            try {
                val response = apiRepository.fetchFilteredProperties(
                    token = token,
                    location = location.takeIf { location != null } ?: "",
                    rooms  = rooms.takeIf { rooms != null } ?: "",
                    categoryId = categoryId.takeIf { categoryId != null } ?: ""
                )
                if(response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            fetchingStatus = FetchingStatus.SUCCESS,
                            properties = response.body()?.data?.properties!!
                        )
                    }
                    Log.i("PROPERTIES_FETCHED_SUCCESSFULLY", response.body()?.data.toString())
                } else {
                    _uiState.update {
                        it.copy(
                            fetchingStatus = FetchingStatus.FAILURE
                        )
                    }
                    Log.e("PROPERTIES_FETCHING_FAILED", response.body().toString())
                }

            }catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        fetchingStatus = FetchingStatus.FAILURE
                    )
                }
                Log.e("PROPERTIES_FETCHING_FAILED_EXCEPTION", e.message.toString())
            }
        }
    }

    fun fetchFilteredProperties(location: String?, rooms: Int?, categoryId: Int?, categoryName: String?) {
        _uiState.update {
            it.copy(
                numberOfRoomsSelected = rooms.toString().takeIf { rooms != null } ?: _uiState.value.numberOfRoomsSelected,
                categoryNameSelected = categoryName.takeIf { categoryName != null } ?: _uiState.value.categoryNameSelected,
                categoryIdSelected = categoryId.toString().takeIf { categoryId != null } ?: _uiState.value.categoryIdSelected,
                location = location.takeIf { location != null } ?: _uiState.value.location,
            )
        }
        fetchProperties(
            token = _uiState.value.userDetails.token,
            location = _uiState.value.location,
            rooms = _uiState.value.numberOfRoomsSelected,
            categoryId = _uiState.value.categoryIdSelected
        )

    }



    fun loadStartupData() {
        loadUserDetails()
        fetchCategories(_uiState.value.userDetails.token)
//        fetchProperties(
//            token = _uiState.value.userDetails.token,
//            categoryId = 0
//        )
    }

    init {
        loadStartupData()
    }
}