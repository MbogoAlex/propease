package com.propertymanagement.tms.ui.screens.propertyAdvertisementPages

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanagement.tms.apiModel.Category
import com.propertymanagement.tms.apiModel.PropertyData
import com.propertymanagement.tms.apiModel.PropertyImage
import com.propertymanagement.tms.apiModel.PropertyLocation
import com.propertymanagement.tms.apiModel.PropertyOwner
import com.propertymanagement.tms.apiModel.PropertyUploadRequestBody
import com.propertymanagement.tms.network.ApiRepository
import com.propertymanagement.tms.propEaseDataStore.DSRepository
import com.propertymanagement.tms.utils.ReusableFunctions
import com.propertymanagement.tms.utils.ReusableFunctions.toLoggedInUserData
import com.tms.propertymanagement.utils.propertyCategoryData
import com.tms.propertymanagement.utils.propertyData
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

enum class UpdatingStatus {
    INITIAL,
    LOADING,
    SUCCESS,
    FAILURE
}

enum class FetchingUpdateCategoriesStatus {
    INITIAL,
    LOADING,
    SUCCESS,
    FAILURE
}



data class PropertyUpdateScreenUiState(
    val numberOfRooms: String = "",
    val category: Category = propertyCategoryData,
    val categoryName: String = "",
    val categories: List<Category> = emptyList(),
    val title: String = "",
    val description: String = "",
    val price: String = "0",
    val county: String = "",
    val address: String = "",
    val features: List<String> = emptyList(),
    val images: List<Uri> = emptyList(),
    val rooms: List<String> = emptyList(),
    val serverImages: List<PropertyImage> = emptyList(),
    val userDetails: ReusableFunctions.LoggedInUserData = ReusableFunctions.LoggedInUserData(),
    val uploadingStatus: UploadingStatus = UploadingStatus.INITIAL,
    val fetchingUpdateCategoriesStatus: FetchingUpdateCategoriesStatus = FetchingUpdateCategoriesStatus.INITIAL,
    val property: PropertyData = propertyData,
    val saveButtonEnabled: Boolean = false,
    val imageUpdateResponse: String = "",
    val propertyTextUpdateResponse: String = "",
    val propertyUploadResponse: String = ""
)
class PropertyUpdateScreenViewModel(
    private val apiRepository: ApiRepository,
    private val dsRepository: DSRepository,
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {
    private val _uiState = MutableStateFlow(value = PropertyUpdateScreenUiState())
    val uiState: StateFlow<PropertyUpdateScreenUiState> = _uiState.asStateFlow()

    private val propertyId: String? = savedStateHandle[PropertyUpdateScreenDestination.propertyId]

    var serverImages = mutableStateListOf<PropertyImage>()
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

    fun updateNumberOfRoomsSelected(numberOfRooms: String) {
        var noRooms = ""
        when(numberOfRooms.lowercase()) {
            "bedsitter" -> noRooms = "Bedsitter"
            "one bedroom" -> noRooms = "One bedroom"
            "two bedrooms" -> noRooms = "Two bedrooms"
            "three bedrooms" -> noRooms = "Three bedrooms"
            "four bedrooms" -> noRooms = "Four bedrooms"
            "five bedrooms" -> noRooms = "Five bedrooms"
            "bedsitter - rental, airbnb, on sale" -> noRooms = "Bedsitter"
            "one bedroom - rental, airbnb, on sale" -> noRooms = "One bedroom"
            "two bedrooms - rental, airbnb, on sale" -> noRooms = "Two bedrooms"
            "three bedrooms - rental, airbnb, on sale" -> noRooms = "Three bedrooms"
            "four bedrooms - rental, airbnb, on sale" -> noRooms = "Four bedrooms"
            "five bedrooms - rental, airbnb, on sale" -> noRooms = "Five bedrooms"
            "single room - shop, office, on sale" -> noRooms = "Single room"
            "two rooms - shop, office, on sale" -> noRooms = "Two rooms"
            "three rooms - shop, office, on sale" -> noRooms = "Three rooms"
            "single room" -> noRooms = "Single room"
            "two rooms" -> noRooms = "Two rooms"
            "three rooms" -> noRooms = "Three rooms"
        }
        _uiState.update {
            it.copy(
                numberOfRooms = noRooms,
                saveButtonEnabled = requiredFieldsFilled(),
            )
        }
    }

    fun updateCategoryType(category: Category) {
        var selectableRooms: List<String> = emptyList()

        when(category.name.lowercase()) {
            "rental" -> {
                selectableRooms = listOf(
                    "Bedsitter",
                    "One bedroom",
                    "Two bedrooms",
                    "Three bedrooms",
                    "Four bedrooms",
                    "Five bedrooms"
                )
            }
            "arbnb" -> {
                selectableRooms = listOf(
                    "Bedsitter",
                    "One bedroom",
                    "Two bedrooms",
                    "Three bedrooms",
                    "Four bedrooms",
                    "Five bedrooms"
                )
            }
            "airbnb" -> {
                selectableRooms = listOf(
                    "Bedsitter",
                    "One bedroom",
                    "Two bedrooms",
                    "Three bedrooms",
                    "Four bedrooms",
                    "Five bedrooms"
                )
            }
            "on sale" -> {
                selectableRooms = listOf(
                    "Bedsitter - Rental, Airbnb, On sale",
                    "One bedroom - Rental, Airbnb, On sale",
                    "Two bedrooms - Rental, Airbnb, On sale",
                    "Three bedrooms - Rental, Airbnb, On sale",
                    "Four bedrooms - Rental, Airbnb, On sale",
                    "Five bedrooms - Rental, Airbnb, On sale",
                    "Single room - Shop, Office, On sale",
                    "Two rooms - Shop, Office, On sale",
                    "Three rooms - Shop, Office, On sale",
                )

            }
            "shop" -> {
                selectableRooms = listOf(
                    "Single room",
                    "Two rooms",
                    "Three rooms",
                )

            }
            "office" -> {
                selectableRooms = listOf(
                    "Single room",
                    "Two rooms",
                    "Three rooms",
                )
            }
        }
        _uiState.update {
            it.copy(
                category = category,
                categoryName = category.name,
                numberOfRooms = "",
                rooms = selectableRooms,
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
                fetchingUpdateCategoriesStatus = FetchingUpdateCategoriesStatus.LOADING
            )
        }
        viewModelScope.launch {
            try {
                val response = apiRepository.fetchCategories()
                if(response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            categories = response.body()?.data?.categories!!,
                            fetchingUpdateCategoriesStatus = FetchingUpdateCategoriesStatus.SUCCESS
                        )
                    }
                    Log.i("CATEGORIES_FETCHED", "${response.body()?.data?.categories}, 1st Category: ${response.body()?.data?.categories!![0]}")
                } else {
                    _uiState.update {
                        it.copy(
                            fetchingUpdateCategoriesStatus = FetchingUpdateCategoriesStatus.FAILURE
                        )
                    }
                    Log.e("CATEGORIES_NOT_FETCHED", response.body().toString())
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        fetchingUpdateCategoriesStatus = FetchingUpdateCategoriesStatus.FAILURE
                    )
                }
                Log.e("CATEGORIES_NOT_FETCHED_EXCEPTION", e.message.toString())
            }
        }
    }

    fun fetchProperty() {
//        _uiState.update {
//            it.copy(
//                fetchingStatus = PropertyFetchingStatus.LOADING
//            )
//        }
        viewModelScope.launch {
            try {
                Log.i("FETCHING_WITH_TOKEN", _uiState.value.userDetails.token)
                val response = apiRepository.fetchSpecificProperty(propertyId = propertyId!!)
                if(response.isSuccessful) {
                    features.addAll(response.body()?.data?.property!!.features)
                    serverImages.addAll(response.body()?.data?.property!!.images)
                    _uiState.update {
                        it.copy(
                            property = response.body()?.data?.property!!,
                            categoryName = response.body()?.data?.property?.category!!,
                            numberOfRooms = response.body()?.data?.property!!.rooms,
                            title = response.body()?.data?.property!!.title,
                            description = response.body()?.data?.property!!.description,
                            price = response.body()?.data?.property!!.price.toString(),
                            county = response.body()?.data?.property!!.location.county,
                            address = response.body()?.data?.property!!.location.address,
                            serverImages = serverImages,
                            features = features,
                        )
                    }
                    checkIfAllFieldsAreFilled()
                    Log.i("PROPERTIES_FETCHED_IMAGES_ARE", _uiState.value.property.images.toString())
                } else {
//                    _uiState.update {
//                        it.copy(
//                            fetchingStatus = PropertyFetchingStatus.FAIL
//                        )
//                    }
                    Log.e("FAILED_TO_FETCH_PROPERTY", response.toString())
                }
            } catch (e: Exception) {
//                _uiState.update {
//                    it.copy(
//                        fetchingStatus = PropertyFetchingStatus.FAIL
//                    )
//                }
                Log.e("FAILED_TO_FETCH_PROPERTY_EXCEPTION", e.message.toString())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateProperty(context: Context) {
        _uiState.update {
            it.copy(
                uploadingStatus = UploadingStatus.LOADING
            )
        }
        val propertyLocation = PropertyLocation(
            county = _uiState.value.county,
            address = _uiState.value.address,
            latitude = 2.0,
            longitude = 2.0
        )

        val property = PropertyUploadRequestBody(
            title = _uiState.value.title,
            categoryId = _uiState.value.categoryName,
            description = _uiState.value.description,
            price = _uiState.value.price.toDouble(),
            rooms = _uiState.value.numberOfRooms,
            postedDate = _uiState.value.property.postedDate,
            location = propertyLocation,
            features = _uiState.value.features,

            )

        viewModelScope.launch {
            try {
                val response = apiRepository.updateProperty(
                    token = _uiState.value.userDetails.token,
                    propertyId = propertyId!!,
                    property = property,
                )
                if(response.isSuccessful) {
                    if(_uiState.value.images.isNotEmpty()) {
                        _uiState.update {
                            it.copy(
                                propertyTextUpdateResponse = "Text updated"
                            )
                        }
                        updateImages(context)
                    } else {
                        _uiState.update {
                            it.copy(
                                uploadingStatus = UploadingStatus.SUCCESS,
                                propertyTextUpdateResponse = "Property updated",
                            )
                        }
                    }


                    Log.i("PROPERTY_UPDATE", "SUCCESS")
                } else {
                    _uiState.update {
                        it.copy(
                            uploadingStatus = UploadingStatus.FAILURE,
                            propertyTextUpdateResponse = "Failed to update property",
                            propertyUploadResponse = response.toString()
                        )
                    }
                    Log.e("FAILED_TO_UPDATE_PROPERTY", "$response, TOKEN: ${_uiState.value.userDetails.token}, Property: $property")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        uploadingStatus = UploadingStatus.FAILURE,
                        propertyTextUpdateResponse = "Failed to update property",
                        propertyUploadResponse = e.toString()
                    )
                }
                Log.e("FAILED_TO_UPDATE_PROPERTY_EXCEPTION", e.message.toString())
            }
        }
    }

    fun updateImages(context: Context) {
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
                val imagePart = MultipartBody.Part.createFormData("files", "upload.$extension", requestFile)
                imageParts.add(imagePart)
            }
        }
        viewModelScope.launch {
            try {
                val response = apiRepository.uploadPropertyImages(
                    token = _uiState.value.userDetails.token,
                    propertyId = propertyId!!,
                    images = imageParts
                )
                if(response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            uploadingStatus = UploadingStatus.SUCCESS,
                            propertyTextUpdateResponse = "Property updated"
                        )
                    }
                    Log.i("IMAGE_UPDATE_SUCCESS", "SUCCESS")
                } else {
                    _uiState.update {
                        it.copy(
                            uploadingStatus = UploadingStatus.FAILURE,
                            propertyTextUpdateResponse = "Failed to upload images",
                            propertyUploadResponse = response.toString(),
                        )
                    }
                    Log.e("IMAGE_UPDATE_FAILURE", response.toString())
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        uploadingStatus = UploadingStatus.FAILURE,
                        propertyTextUpdateResponse = "Failed to upload images",
                        propertyUploadResponse = e.toString()
                    )
                }
                Log.e("IMAGE_UPDATE_FAILURE_EXCEPTION", e.message.toString())
            }

        }
    }

    fun deletePropertyImages(imageId: String, index: Int) {

        viewModelScope.launch {
            try {
                val response = apiRepository.deletePropertyImage(
                    token = _uiState.value.userDetails.token,
                    imageId = imageId,
                    propertyId = propertyId!!
                )
                if(response.isSuccessful) {
                    serverImages.removeAt(index)
                    _uiState.update {
                        it.copy(
                           serverImages = serverImages)
                    }
                    Log.i("IMAGE_DELETION", "SUCCESS")
                } else {
                    _uiState.update {
                        it.copy(
                            uploadingStatus = UploadingStatus.FAILURE,
                            propertyUploadResponse = response.toString()
                        )
                    }
                    Log.e("IMAGE_DELETION_FAILURE", "$response, TOKEN: ${_uiState.value.userDetails.token}")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        uploadingStatus = UploadingStatus.FAILURE,
                        propertyUploadResponse = e.toString()
                    )
                }
                Log.e("IMAGE_DELETION_FAILURE_EXCEPTION", e.message.toString())
            }
        }
    }

    fun requiredFieldsFilled(): Boolean {
        return _uiState.value.price.isNotEmpty() &&
                _uiState.value.price != "0" &&
                _uiState.value.title.isNotEmpty() &&
                _uiState.value.county.isNotEmpty() &&
                _uiState.value.address.isNotEmpty() &&
                _uiState.value.features.isNotEmpty()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun publishUpdate(context: Context) {
        updateProperty(context)
    }

    fun resetSavingState() {
        _uiState.update {
            it.copy(
                uploadingStatus = UploadingStatus.INITIAL
            )
        }
    }

    fun checkIfAllFieldsAreFilled() {
        val filled = requiredFieldsFilled()
        _uiState.update {
            it.copy(
                saveButtonEnabled = filled
            )
        }
    }

    init {
        loadUserDetails()
        fetchCategories(_uiState.value.userDetails.token)
        fetchProperty()

    }
}