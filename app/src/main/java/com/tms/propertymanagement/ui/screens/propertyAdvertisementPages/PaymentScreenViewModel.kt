package com.tms.propertymanagement.ui.screens.propertyAdvertisementPages

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanagement.tms.apiModel.PropertyData
import com.propertymanagement.tms.network.ApiRepository
import com.propertymanagement.tms.propEaseDataStore.DSRepository
import com.propertymanagement.tms.ui.screens.propertyAdvertisementPages.PropertyFetchingStatus
import com.propertymanagement.tms.utils.ReusableFunctions
import com.propertymanagement.tms.utils.ReusableFunctions.toLoggedInUserData
import com.tms.propertymanagement.apiModel.PaymentRequestBody
import com.tms.propertymanagement.utils.LoadingStatus
import com.tms.propertymanagement.utils.PaymentStatus
import com.tms.propertymanagement.utils.PaymentStatusCheck
import com.tms.propertymanagement.utils.propertyData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PaymentScreenUiState(
    val property: PropertyData = propertyData,
    val userDetails: ReusableFunctions.LoggedInUserData = ReusableFunctions.LoggedInUserData(),
    val loadingStatus: LoadingStatus = LoadingStatus.INITIAL,
    val partnerReferenceID: String = "",
    val paymentStatus: PaymentStatus = PaymentStatus.INITIAL,
    val paymentSuccessful: Boolean = false,
    val paymentStatusCheck: PaymentStatusCheck = PaymentStatusCheck.INITIAL
)
class PaymentScreenViewModel(
    private val apiRepository: ApiRepository,
    private val dsRepository: DSRepository,
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {
    private val _uiState = MutableStateFlow(value = PaymentScreenUiState())
    val uiState: StateFlow<PaymentScreenUiState> = _uiState.asStateFlow()

    private val propertyId: String? = savedStateHandle[PaymentScreenDestination.propertyId]

    fun loadStartupData() {
        viewModelScope.launch {
            dsRepository.dsUserModel.collect() {dsUserModel->
                _uiState.update {
                    it.copy(
                        userDetails = dsUserModel.toLoggedInUserData()
                    )
                }
            }
        }
        fetchProperty()
    }

    fun fetchProperty() {
        _uiState.update {
            it.copy(
                loadingStatus = LoadingStatus.INITIAL,
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
                            loadingStatus = LoadingStatus.SUCCESS
                        )
                    }
                    Log.i("LIVE_PROPERTIES_FETCHED", _uiState.value.property.toString())
                } else {
                    _uiState.update {
                        it.copy(
                            loadingStatus = LoadingStatus.FAIL
                        )
                    }
                    Log.e("FAILED_TO_FETCH_PROPERTY", response.toString())
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loadingStatus = LoadingStatus.FAIL,
                    )
                }
                Log.e("FAILED_TO_FETCH_PROPERTY_EXCEPTION", e.message.toString())
            }
        }
    }
    fun payForPropertyAd() {
        _uiState.update {
            it.copy(
                paymentStatus = PaymentStatus.LOADING
            )
        }
        val paymentRequestBody = PaymentRequestBody(
            propertyId = uiState.value.property.propertyId,
            msisdn = uiState.value.userDetails.phoneNumber,
            transactionAmount = 1.0
        )
        viewModelScope.launch {
            try {
                val response = apiRepository.payForPropertyAd(
                    token = uiState.value.userDetails.token,
                    paymentRequestBody = paymentRequestBody,
                )
                if(response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            partnerReferenceID = response.body()?.data?.data?.partnerReferenceID!!,
                            paymentStatus = PaymentStatus.SUCCESS
                        )
                    }
                    Log.i("PAYMENT", "SUCCESS")
                } else {
                    _uiState.update {
                        it.copy(
                            paymentStatus = PaymentStatus.FAIL
                        )
                    }
                    Log.e("PAYMENT_ERROR_RESPONSE", response.toString())
                }
            }catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        paymentStatus = PaymentStatus.FAIL
                    )
                }
                Log.e("PAYMENT_ERROR_EXCEPTION", e.toString())
            }
        }
    }

    fun getPaymentStatus() {
        _uiState.update {
            it.copy(
                paymentStatusCheck = PaymentStatusCheck.LOADING,
            )
        }
        viewModelScope.launch {
            try {
                val response = apiRepository.getPaymentStatus(
                    token = uiState.value.userDetails.token,
                    partnerTransactionID = uiState.value.partnerReferenceID
                )
                if(response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            paymentStatusCheck = PaymentStatusCheck.SUCCESS,
                            paymentSuccessful = response.body()?.transactionStatus!!.lowercase() != "failed"
                        )
                    }
                    Log.i("PAYMENT_STATUS_CHECK", "SUCCESS")
                } else {
                    _uiState.update {
                        it.copy(
                            paymentStatusCheck = PaymentStatusCheck.FAIL,
                        )
                    }
                    Log.e("PAYMENT_STATUS_CHECK_ERROR_RESPONSE", response.toString())
                }
            }catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        paymentStatusCheck = PaymentStatusCheck.FAIL,
                    )
                }
                Log.e("PAYMENT_STATUS_CHECK_ERROR_EXCEPTION", e.toString())
            }
        }
    }

    fun resetPaymentStatus() {
        _uiState.update {
            it.copy(
                paymentStatus = PaymentStatus.INITIAL,
            )
        }
    }

    fun resetPaymentStatusCheck() {
        _uiState.update {
            it.copy(
                paymentStatusCheck = PaymentStatusCheck.INITIAL
            )
        }
    }

    init {
        loadStartupData()
    }
}