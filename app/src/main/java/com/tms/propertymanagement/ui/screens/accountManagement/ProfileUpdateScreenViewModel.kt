package com.tms.propertymanagement.ui.screens.accountManagement

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanagement.tms.apiModel.ProfileUpdateRequestBody
import com.propertymanagement.tms.network.ApiRepository
import com.propertymanagement.tms.propEaseDataStore.DSRepository
import com.propertymanagement.tms.propEaseDataStore.DSUserModel
import com.propertymanagement.tms.ui.screens.accountManagement.UpdatingStatus
import com.propertymanagement.tms.utils.ReusableFunctions
import com.propertymanagement.tms.utils.ReusableFunctions.toLoggedInUserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class UpdatingUserProfileStatus {
    INITIAL,
    LOADING,
    SUCCESS,
    FAIL
}

data class ProfileUpdateScreenUiState(
    val userDetails: ReusableFunctions.LoggedInUserData = ReusableFunctions.LoggedInUserData(),
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val enableUpdateButton: Boolean = false,
    val updatingUserProfileStatus: UpdatingUserProfileStatus = UpdatingUserProfileStatus.INITIAL
)

class ProfileUpdateScreenViewModel(
    private val apiRepository: ApiRepository,
    private val dsRepository: DSRepository,
): ViewModel() {
    private val _uiState = MutableStateFlow(value = ProfileUpdateScreenUiState())
    val uiState: StateFlow<ProfileUpdateScreenUiState> = _uiState.asStateFlow()

    lateinit var originalFirstName: String
    lateinit var originalLastName: String
    fun loadUserDetails() {
        viewModelScope.launch {
            dsRepository.dsUserModel.collect() {dsUserModel ->
                val (firstName, lastName) = dsUserModel.userName.split(" ", limit = 2)
                originalFirstName = firstName
                originalLastName = lastName
                _uiState.update {
                    it.copy(
                        userDetails = dsUserModel.toLoggedInUserData(),
                        firstName = firstName,
                        lastName = lastName,
                        email = dsUserModel.email,
                        phoneNumber = dsUserModel.phoneNumber
                    )
                }
            }
        }
    }

    fun updateFirstName(firstName: String) {
        _uiState.update {
            it.copy(
                firstName = firstName,
                enableUpdateButton = validFields()
            )
        }
    }

    fun updateLastName(lastName: String) {
        _uiState.update {
            it.copy(
                lastName = lastName,
                enableUpdateButton = validFields()
            )
        }
    }

    fun updateEmail(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                enableUpdateButton = validFields()
            )
        }
    }

    fun updatePhoneNumber(phoneNumber: String) {
        _uiState.update {
            it.copy(
                phoneNumber = phoneNumber,
                enableUpdateButton = validFields()
            )
        }
    }

    fun updateUserProfile() {
        _uiState.update {
            it.copy(
                updatingUserProfileStatus = UpdatingUserProfileStatus.LOADING
            )
        }
        val profile = ProfileUpdateRequestBody(
            email = _uiState.value.email,
            phoneNumber = _uiState.value.phoneNumber,
            password = _uiState.value.userDetails.password,
            fname = _uiState.value.firstName,
            lname = _uiState.value.lastName
        )
        viewModelScope.launch {
            try {
                val response = apiRepository.updateUserProfile(
                    token = _uiState.value.userDetails.token,
                    profileDetails = profile,
                    userId = _uiState.value.userDetails.userId!!.toString()
                )
                if(response.isSuccessful) {
                    val dsUserModel = DSUserModel(
                        userId = _uiState.value.userDetails.userId,
                        userName = "${_uiState.value.firstName} ${_uiState.value.lastName}",
                        phoneNumber = _uiState.value.phoneNumber,
                        email = _uiState.value.email,
                        password = _uiState.value.userDetails.password,
                        token = _uiState.value.userDetails.token
                    )
                    dsRepository.saveUserData(dsUserModel)
                    _uiState.update {
                        it.copy(
                            updatingUserProfileStatus = UpdatingUserProfileStatus.SUCCESS
                        )
                    }
                } else {
                    Log.i("FAILED_TO_UPDATE_USER_PROFILE", response.toString())
                    _uiState.update {
                        it.copy(
                            updatingUserProfileStatus = UpdatingUserProfileStatus.FAIL
                        )
                    }
                }
            } catch (e: Exception) {
                Log.i("FAILED_TO_UPDATE_USER_PROFILE", e.toString())
                _uiState.update {
                    it.copy(
                        updatingUserProfileStatus = UpdatingUserProfileStatus.FAIL
                    )
                }
            }
        }
    }

    fun resetUpdatingStatus() {
        _uiState.update {
            it.copy(
                updatingUserProfileStatus = UpdatingUserProfileStatus.INITIAL
            )
        }
    }

    fun validFields(): Boolean {
        return _uiState.value.firstName.isNotEmpty() &&
                _uiState.value.lastName.isNotEmpty() &&
                _uiState.value.email.isNotEmpty() &&
                _uiState.value.phoneNumber.isNotEmpty()
//                &&
//                (
//                        _uiState.value.firstName != originalFirstName ||
//                        _uiState.value.lastName != originalLastName ||
//                        _uiState.value.email != _uiState.value.userDetails.email ||
//                        _uiState.value.phoneNumber != _uiState.value.userDetails.phoneNumber
//                        )
    }
    init {
        loadUserDetails()
    }
}