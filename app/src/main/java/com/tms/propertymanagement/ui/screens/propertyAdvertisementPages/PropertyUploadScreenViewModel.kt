package com.tms.propertymanagement.ui.screens.propertyAdvertisementPages

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tms.propertymanagement.apiModel.Category
import com.tms.propertymanagement.apiModel.PropertyLocation
import com.tms.propertymanagement.apiModel.PropertyUploadRequestBody
import com.tms.propertymanagement.network.ApiRepository
import com.tms.propertymanagement.propEaseDataStore.DSRepository
import com.tms.propertymanagement.ui.screens.appContentPages.FetchingStatus
import com.tms.propertymanagement.utils.ReusableFunctions
import com.tms.propertymanagement.utils.ReusableFunctions.toLoggedInUserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.time.LocalDateTime

enum class UploadingStatus {
    INITIAL,
    LOADING,
    SUCCESS,
    FAILURE
}

enum class FetchingCategoriesStatus {
    INITIAL,
    LOADING,
    SUCCESS,
    FAILURE
}

val categoryData = Category(
    id = 0,
    name = ""
)
data class PropertyUploadScreenUiState(
    val numberOfRooms: Int = 0,
    val category: Category = categoryData,
    val categories: List<Category> = emptyList(),
    val title: String = "",
    val description: String = "",
    val price: String = "0",
    val county: String = "",
    val address: String = "",
    val features: List<String> = emptyList(),
    val images: List<Uri> = emptyList(),
    val userDetails: ReusableFunctions.LoggedInUserData = ReusableFunctions.LoggedInUserData(),
    val uploadingStatus: UploadingStatus = UploadingStatus.INITIAL,
    val fetchingCategoriesStatus: FetchingCategoriesStatus = FetchingCategoriesStatus.INITIAL,
    val saveButtonEnabled: Boolean = false
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

    val features = mutableStateListOf<String>()
    val images = mutableStateListOf<Uri>()

    fun updateNumberOfRoomsSelected(numberOfRooms: Int) {
        _uiState.update {
            it.copy(
                numberOfRooms = numberOfRooms,
                saveButtonEnabled = requiredFieldsFilled()
            )
        }
    }

    fun updateCategoryType(category: Category) {
        _uiState.update {
            it.copy(
                category = category,
                saveButtonEnabled = requiredFieldsFilled()
            )
        }
    }

    fun updateTitle(title: String) {
        _uiState.update {
            it.copy(
                title = title,
                saveButtonEnabled = requiredFieldsFilled()
            )
        }
    }

    fun updatePrice(price: String) {
        _uiState.update {
            it.copy(
                price = price,
                saveButtonEnabled = requiredFieldsFilled()
            )
        }
    }

    fun updateCounty(county: String) {
        _uiState.update {
            it.copy(
                county = county,
                saveButtonEnabled = requiredFieldsFilled()
            )
        }
    }

    fun updateAddress(address: String) {
        _uiState.update {
            it.copy(
                address = address,
                saveButtonEnabled = requiredFieldsFilled()
            )
        }
    }

    fun updateDescription(description: String) {
        _uiState.update {
            it.copy(
                description = description,
                saveButtonEnabled = requiredFieldsFilled()
            )
        }
    }

    fun addFeatureField() {
        features.add("")
        _uiState.update {
            it.copy(
                features = features,
                saveButtonEnabled = requiredFieldsFilled()
            )
        }
    }

    fun removeFeatureField(index: Int) {
        features.removeAt(index)
        _uiState.update {
            it.copy(
                features = features,
                saveButtonEnabled = requiredFieldsFilled()
            )
        }
    }

    fun updateFeature(index: Int, value: String) {
        features[index] = value
        _uiState.update {
            it.copy(
                features = features,
                saveButtonEnabled = requiredFieldsFilled()
            )
        }
    }

    fun uploadPhoto(uri: Uri) {
        images.add(uri)
        _uiState.update {
            it.copy(
                images = images,
                saveButtonEnabled = requiredFieldsFilled()
            )
        }
    }

    fun removePhoto(index: Int) {
        images.removeAt(index)
        _uiState.update {
            it.copy(
                images = images,
                saveButtonEnabled = requiredFieldsFilled()
            )
        }
    }

    fun fetchCategories(token: String) {
        _uiState.update {
            it.copy(
                fetchingCategoriesStatus = FetchingCategoriesStatus.LOADING
            )
        }
        viewModelScope.launch {
            try {
                val response = apiRepository.fetchCategories(token)
                if(response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            categories = response.body()?.data?.categories!!,
                            fetchingCategoriesStatus = FetchingCategoriesStatus.SUCCESS
                        )
                    }
                    Log.i("CATEGORIES_FETCHED", "${response.body()?.data?.categories}, 1st Category: ${response.body()?.data?.categories!![0]}")
                } else {
                    _uiState.update {
                        it.copy(
                            fetchingCategoriesStatus = FetchingCategoriesStatus.FAILURE
                        )
                    }
                    Log.e("CATEGORIES_NOT_FETCHED", response.body().toString())
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        fetchingCategoriesStatus = FetchingCategoriesStatus.FAILURE
                    )
                }
                Log.e("CATEGORIES_NOT_FETCHED_EXCEPTION", e.message.toString())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadProperty(context: Context) {
        _uiState.update {
            it.copy(
                uploadingStatus = UploadingStatus.LOADING
            )
        }
        val propertyLocation = PropertyLocation(
            county = _uiState.value.county,
            address = _uiState.value.address,
            latitude = 0.0,
            longitude = 0.0
        )

        val property = PropertyUploadRequestBody(
            title = _uiState.value.title,
            description = _uiState.value.description,
            categoryId = _uiState.value.category.name,
            price = _uiState.value.price.toDouble(),
            rooms = _uiState.value.numberOfRooms,
            availableDate = LocalDateTime.now().toString(),
            location = propertyLocation,
            features = _uiState.value.features,

        )

        var imageParts = ArrayList<MultipartBody.Part>()
        _uiState.value.images.forEach { uri ->
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r", null)
            parcelFileDescriptor?.let { pfd ->
                val inputStream = FileInputStream(pfd.fileDescriptor)
                val byteArrayOutputStream = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var length: Int
                while(inputStream.read(buffer).also { length = it } != -1) {
                    byteArrayOutputStream.write(buffer, 0, length)
                }
                val byteArray = byteArrayOutputStream.toByteArray()

                //Get the MIME type of the file

                val mimeType = context.contentResolver.getType(uri)
                val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
                val requestFile = RequestBody.create(mimeType?.toMediaTypeOrNull(), byteArray)
                val imagePart = MultipartBody.Part.createFormData("imageFiles", "upload.$extension", requestFile)
                imageParts.add(imagePart)
            }
        }

        viewModelScope.launch {
            try {
                val response = apiRepository.uploadProperty(
                    token = _uiState.value.userDetails.token,
                    userId = _uiState.value.userDetails.userId!!,
                    property = property,
                    images = imageParts
                )
                if(response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            uploadingStatus = UploadingStatus.SUCCESS
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            uploadingStatus = UploadingStatus.FAILURE
                        )
                    }
                    Log.e("FAILED_TO_UPLOAD_PROPERTY", response.message())
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        uploadingStatus = UploadingStatus.FAILURE
                    )
                }
                Log.e("FAILED_TO_UPLOAD_PROPERTY_EXCEPTION", e.message.toString())
            }
        }
    }

    fun requiredFieldsFilled(): Boolean {
        return _uiState.value.price.isNotEmpty() &&
                _uiState.value.price != "0" &&
                _uiState.value.title.isNotEmpty() &&
                _uiState.value.county.isNotEmpty() &&
                _uiState.value.address.isNotEmpty() &&
                _uiState.value.features.isNotEmpty() &&
                _uiState.value.images.isNotEmpty() &&
                _uiState.value.numberOfRooms  != 0 &&
                _uiState.value.category.id != 0
    }

    fun resetSavingState() {
        _uiState.update {
            it.copy(
                uploadingStatus = UploadingStatus.INITIAL
            )
        }
    }

    init {
        loadUserDetails()
        fetchCategories(_uiState.value.userDetails.token)
    }
}