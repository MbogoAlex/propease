package com.propertymanagement.tms.ui.screens.appContentPages

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanagement.tms.apiModel.Category
import com.propertymanagement.tms.apiModel.PropertyData
import com.propertymanagement.tms.network.ApiRepository
import com.propertymanagement.tms.propEaseDataStore.DSRepository
import com.propertymanagement.tms.utils.ReusableFunctions
import com.propertymanagement.tms.utils.ReusableFunctions.toLoggedInUserData
import com.tms.propertymanagement.db.DBRepository
import com.tms.propertymanagement.db.Feature
import com.tms.propertymanagement.db.Location
import com.tms.propertymanagement.db.Owner
import com.tms.propertymanagement.db.PaymentDetails
import com.tms.propertymanagement.db.Property
import com.tms.propertymanagement.db.PropertyDetails
import com.tms.propertymanagement.utils.toCategory
import com.tms.propertymanagement.utils.toPropertyData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
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
    val filteringOn: Boolean = false,
    val location: String = "",
    val offlineProperties: List<PropertyDetails> = emptyList(),
    val previousConnection: Boolean = false,
    val currentConnection: Boolean = false,
    val isConnected: Boolean = false,
    val dataInsertedIntoDB: Boolean = false,
    val internetPresent: Boolean = true
)
class ListingsScreenViewModel(
    private val apiRepository: ApiRepository,
    private val dsRepository: DSRepository,
    private val dbRepository: DBRepository,
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

    fun fetchCategories() {
        if(!_uiState.value.internetPresent) {
            _uiState.update {
                it.copy(
                    location = "",
                    categoryNameSelected = "",
                    categoryIdSelected = "",
                    numberOfRoomsSelected = "",
                    filteringOn = false
                )
            }
        }
        Log.i("FETCHING_PROPERTIES", "FETCHING PROPERTIS")
        _uiState.update {
            it.copy(
                fetchingStatus = FetchingStatus.LOADING
            )
        }
        viewModelScope.launch {
            try {
                val response = apiRepository.fetchCategories()
                if(response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            categories = response.body()?.data?.categories!!,
//                            categoryNameSelected = response.body()?.data?.categories!![0].name,
                            fetchingStatus = FetchingStatus.SUCCESS,
                            internetPresent = true
                        )
                    }
                    fetchProperties(
                        token = _uiState.value.userDetails.token,
                        location = null,
                        rooms = null,
                        categoryId = null
                    )

                    Log.i("CATEGORIES_FETCHED", "${response.body()?.data?.categories}, 1st Category: ${response.body()?.data?.categories!![0]}")
                } else {
                    _uiState.update {
                        it.copy(
                            fetchingStatus = FetchingStatus.FAILURE
                        )
                    }
                    Log.e("CATEGORIES_NOT_FETCHED", response.toString())
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        fetchingStatus = FetchingStatus.FAILURE,
                        internetPresent = false
                    )
                }

                if(_uiState.value.isConnected) {
                    fetchFilteredDBProperties(
                        location = null,
                        categoryName = null,
                        categoryId = null,
                        rooms = null
                    )
                }
//                fetchFilteredDBProperties(
//                    location = null,
//                    rooms = null,
//                    categoryId = null,
//                    categoryName = null
//                )
                Log.e("CATEGORIES_NOT_FETCHED_EXCEPTION", e.toString())
            }
        }
    }

    fun fetchProperties(token: String, location: String?, rooms: String?, categoryId: String?) {

        viewModelScope.launch {
            try {
                val response = apiRepository.fetchFilteredProperties(

                    location = location.takeIf { location != null } ?: "",
                    rooms  = rooms.takeIf { rooms != null } ?: "",
                    categoryId = categoryId.takeIf { categoryId != null } ?: ""
                )
                if(response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            fetchingStatus = FetchingStatus.SUCCESS,
                            properties = response.body()?.data?.properties!!,
                            internetPresent = true
                        )
                    }
                    if(!_uiState.value.dataInsertedIntoDB) {
                        insertPropertyIntoDB()
                        _uiState.update {
                            it.copy(
                                dataInsertedIntoDB = true
                            )
                        }
                    }

                    Log.i("PROPERTIES_FETCHED_SUCCESSFULLY", response.toString())
                } else {
                    _uiState.update {
                        it.copy(
                            fetchingStatus = FetchingStatus.FAILURE
                        )
                    }
                    Log.e("PROPERTIES_FETCHING_FAILED", response.toString())
                }

            }catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        fetchingStatus = FetchingStatus.FAILURE,
                        internetPresent = false
                    )
                }
                if(_uiState.value.isConnected) {
                    fetchFilteredDBProperties(
                        location = null,
                        rooms = null,
                        categoryId = null,
                        categoryName = null
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

    fun unfilter() {
        _uiState.update {
            it.copy(
                numberOfRoomsSelected = "",
                categoryNameSelected = "",
                categoryIdSelected = "",
                location = "",
                filteringOn = false
            )
        }

        if(_uiState.value.isConnected) {
            fetchProperties(
                token = _uiState.value.userDetails.token,
                categoryId = "",
                location = "",
                rooms =  ""
            )
        } else if(!_uiState.value.isConnected) {
            fetchFilteredDBProperties(
                location = null,
                categoryName = null,
                categoryId = null,
                rooms = null
            )
        }
    }

    fun turnOnFiltering() {
        _uiState.update {
            it.copy(
                filteringOn = true
            )
        }
    }

    fun switchFilteringStatus() {
        _uiState.update {
            it.copy(
                filteringOn = !(_uiState.value.filteringOn)
            )
        }
    }

    fun loadStartupData() {
        loadUserDetails()
    }

    // Room database
    fun insertPropertyIntoDB() {
        viewModelScope.launch {
            for(prop in _uiState.value.properties) {
                var categoryId: Int = 0
                var categoryName: String = ""
                for(category in _uiState.value.categories) {
                    if(category.name.lowercase() == prop.category.lowercase()) {
                        categoryId = category.id
                        categoryName = category.name
                    }
                }

                val category: com.tms.propertymanagement.db.Category = com.tms.propertymanagement.db.Category(
                    categoryId = categoryId,
                    name = categoryName
                )

                val owner: Owner = Owner(
                    firstName = prop.user.fname,
                    lastName = prop.user.lname,
                    phoneNumber = prop.user.phoneNumber,
                    email = prop.user.email,
                    ownerId = prop.user.userId,
                    propertyId = prop.propertyId
                )

                val property: Property = Property(
                    propertyId = prop.propertyId,
                    title = prop.title,
                    description = prop.description,
                    postedDate = prop.postedDate,
                    deletionTime = prop.deletionTime ?: "",
                    propertyLocation = "${prop.location.county}, ${prop.location.address}",
                    price = prop.price,
                    rooms = prop.rooms,
                    ownerId = prop.user.userId,
                    paid = prop.paid,
                    approved = prop.approved,
                    categoryId = categoryId,
                    categoryName = prop.category,
                    fetchData = true
                )

                val paymentDetails = PaymentDetails(
                    paymentId = prop.paymentDetails.id,
                    propertyId = prop.propertyId,
                    partnerReferenceID = prop.paymentDetails.partnerReferenceID,
                    transactionID = prop.paymentDetails.transactionID,
                    msisdn = prop.paymentDetails.msisdn,
                    partnerTransactionID = prop.paymentDetails.partnerTransactionID,
                    payerTransactionID = prop.paymentDetails.payerTransactionID,
                    receiptNumber = prop.paymentDetails.receiptNumber,
                    transactionAmount = prop.paymentDetails.transactionAmount,
                    transactionStatus = prop.paymentDetails.transactionStatus,
                    paymentComplete = prop.paymentDetails.paymentComplete,
                    createdAt = prop.paymentDetails.createdAt,
                    updatedAt = prop.paymentDetails.updatedAt
                )

                val location: Location = Location(
                    propertyId = prop.propertyId,
                    county = prop.location.county,
                    address = prop.location.address,
                    longitude = prop.location.longitude,
                    latitude = prop.location.latitude
                )

                val features = mutableListOf<Feature>()
                for(item in prop.features) {
                    val feature: Feature = Feature(
                        name = item,
                        propertyId = prop.propertyId
                    )
                    features.add(feature)

                }



                // insert category

                try {
                    dbRepository.insertCategory(category)
                } catch (e: Exception) {
                    Log.e("INSERT_CATEGORY_ERROR", e.toString())
                }

                // insert owner

                try {
                    dbRepository.insertOwner(owner)
                } catch (e: Exception) {
                    Log.e("FAILED_TO_INSERT_OWNER", e.toString())
                }

                // insert property

                try {
                    dbRepository.insertProperty(property)
                } catch (e: Exception) {
                    Log.e("FAILED_TO_INSERT_PROPERTY", e.toString())
                }

                // insert location

                try {
                    dbRepository.insertLocation(location)
                } catch (e: Exception) {
                    Log.e("FAILED_TO_INSERT_LOCATION", e.toString())
                }

                // insert payment details

                try {
                    dbRepository.insertPaymentDetails(paymentDetails)
                } catch (e: Exception) {
                    Log.e("FAILED_TO_INSERT_PAYMENT_DETAILS", e.toString())
                }

                // insert features

                for (feature in features) {
                    try {
                        dbRepository.insertFeature(feature)
                    } catch (e: Exception) {
                        Log.e("FAILED_TO_INSERT_FEATURE", e.toString())
                    }
                }

            }
        }

    }

    fun fetchPropertiesFromDB() {
        viewModelScope.launch {
            try {
                dbRepository.getAllProperties().collect() {properties ->


                    _uiState.update {
                        it.copy(
                            offlineProperties = properties,
                            properties = properties.map {propertyDetails ->
                                propertyDetails.toPropertyData(propertyDetails)
                            },
                            fetchingStatus = FetchingStatus.SUCCESS
                        )
                    }
                }
            } catch (e: Exception) {}
        }
    }

    fun setConnectionStatus(isConnected: Boolean) {
        _uiState.update {
            it.copy(
                isConnected = isConnected
            )
        }
        if(isConnected) {
            _uiState.update {
                it.copy(
                    numberOfRoomsSelected = "",
                    categoryNameSelected = "",
                    categoryIdSelected = "",
                    location = "",
                    filteringOn = false
                )
            }
            fetchCategories()
        } else if(!isConnected) {
            _uiState.update {
                it.copy(
                    numberOfRoomsSelected = "",
                    categoryNameSelected = "",
                    categoryIdSelected = "",
                    location = "",
                    filteringOn = false
                )
            }
            fetchFilteredDBProperties(
                location = null,
                categoryName = null,
                categoryId = null,
                rooms = null
            )
        }

    }

    private val filteredCategories = mutableListOf<Category>()

    fun fetchFilteredDBProperties(location: String?, rooms: Int?, categoryId: Int?, categoryName: String?) {
        Log.i("CONNECTION_STATUS", "${_uiState.value.isConnected}")
        _uiState.update {
            it.copy(
                numberOfRoomsSelected = rooms.toString().takeIf { rooms != null } ?: _uiState.value.numberOfRoomsSelected,
                categoryNameSelected = categoryName.takeIf { categoryName != null } ?: _uiState.value.categoryNameSelected,
                categoryIdSelected = categoryId.toString().takeIf { categoryId != null } ?: _uiState.value.categoryIdSelected,
                location = location.takeIf { location != null } ?: _uiState.value.location
            )

        }
        Log.i("LOCATION", _uiState.value.location)
        Log.i("ROOMS", _uiState.value.numberOfRoomsSelected)
        Log.i("CATEGORYID", categoryId.toString())
        Log.i("CATEGORYNAME", _uiState.value.categoryNameSelected)
        viewModelScope.launch {
            try {
                Log.i("FETCHING_FROM_DB", "Fetching from db")
                dbRepository.filterProperties(
                    rooms = if(_uiState.value.numberOfRoomsSelected.isEmpty()) null else _uiState.value.numberOfRoomsSelected.toInt(),
                    category = _uiState.value.categoryNameSelected.takeIf { it.isNotEmpty() },
//                    location = "kiambu",
                    location = _uiState.value.location.takeIf { it.isNotEmpty() },
                    fetchData = true
                ).collect() {propertyDetails ->
                    if(!_uiState.value.isConnected || !_uiState.value.internetPresent) {
                        Log.i("PROP_ROOMS_SIZE", propertyDetails.size.toString())

                        val unfilteredCategories = propertyDetails.map {
                            it.category.toCategory()
                        }

                        if(_uiState.value.categories.isEmpty()) {
                            unfilteredCategories.forEachIndexed { index, category ->
                                if(!filteredCategories.contains(category)) {
                                    filteredCategories.add(category)
                                }
                            }
                        }
                        for (property in propertyDetails) {
                            Log.i("OFFLINE_PROPERTY", property.toString())
                        }
                        _uiState.update {
                            it.copy(
                                categories = filteredCategories,
                                properties = propertyDetails.map { property -> property.toPropertyData(property)},
                                fetchingStatus = FetchingStatus.SUCCESS
                            )
                        }
                    }

                }
            } catch (e: Exception) {}

        }

    }

    init {
        loadStartupData()
    }
}

