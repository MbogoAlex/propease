package com.tms.propertymanagement.ui.screens.propertyAdvertisementPages

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tms.propertymanagement.apiModel.Category
import com.tms.propertymanagement.network.ApiRepository
import com.tms.propertymanagement.propEaseDataStore.DSRepository
import com.tms.propertymanagement.utils.ReusableFunctions
import com.tms.propertymanagement.utils.ReusableFunctions.toLoggedInUserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val categoryData = Category(
    id = 0,
    name = ""
)
data class PropertyUploadScreenUiState(
    val numberOfRooms: Int = 0,
    val category: Category = categoryData,
    val title: String = "",
    val description: String = "",
    val features: List<String> = emptyList(),
    val images: List<Uri> = emptyList(),
    val userDetails: ReusableFunctions.LoggedInUserData = ReusableFunctions.LoggedInUserData()
)

class PropertyUploadScreenViewModel(
    private val apiRepository: ApiRepository,
    private val dsRepository: DSRepository
) :ViewModel() {
    private val _uiState = MutableStateFlow(value = PropertyUploadScreenUiState())
    val uiState: StateFlow<PropertyUploadScreenUiState> = _uiState.asStateFlow()

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

    val features = mutableStateListOf<String>("")
    val images = mutableStateListOf<Uri>()

    fun updateNumberOfRoomsSelected(numberOfRooms: Int) {
        _uiState.update {
            it.copy(
                numberOfRooms = numberOfRooms
            )
        }
    }

    fun updateCategoryType(category: Category) {
        _uiState.update {
            it.copy(
                category = category
            )
        }
    }

    fun updateRoomTitle(title: String) {
        _uiState.update {
            it.copy(
                title = title
            )
        }
    }

    fun updateRoomDescription(description: String) {
        _uiState.update {
            it.copy(
                description = description
            )
        }
    }

    fun addFeatureField() {
        features.add("")
        _uiState.update {
            it.copy(
                features = features
            )
        }
    }

    fun removeFeatureField(index: Int) {
        features.removeAt(index)
        _uiState.update {
            it.copy(
                features = features
            )
        }
    }

    fun updateFeature(index: Int, value: String) {
        features[index] = value
        _uiState.update {
            it.copy(
                features = features
            )
        }
    }

    fun uploadPhoto(uri: Uri) {
        images.add(uri)
        _uiState.update {
            it.copy(
                images = images
            )
        }
    }

    fun removePhoto(index: Int) {
        images.removeAt(index)
        _uiState.update {
            it.copy(
                images = images
            )
        }
    }

    

    init {
        loadUserDetails()
    }
}