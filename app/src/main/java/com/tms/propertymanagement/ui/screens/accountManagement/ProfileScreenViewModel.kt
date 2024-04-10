package com.propertymanagement.tms.ui.screens.accountManagement

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanagement.tms.apiModel.ProfileUpdateRequestBody
import com.propertymanagement.tms.network.ApiRepository
import com.propertymanagement.tms.propEaseDataStore.DSRepository
import com.propertymanagement.tms.propEaseDataStore.DSUserModel
import com.propertymanagement.tms.utils.ReusableFunctions
import com.propertymanagement.tms.utils.ReusableFunctions.toLoggedInUserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class UpdatingStatus{
    INITIAL,
    LOADING,
    SUCCESS,
    FAILURE
}
data class ProfileDetails(
    val email: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val fname: String = "",
    val lname: String = ""
)

data class ProfileScreenUiState(
    val userDetails: ReusableFunctions.LoggedInUserData = ReusableFunctions.LoggedInUserData(),
    val updatedUserDetails: ProfileDetails = ProfileDetails(),
    val showUpdateProfileScreen: Boolean = false,
    val enableUpdateButton: Boolean = false,
    val uploadedPicture: Uri = Uri.EMPTY,
    val updatingStatus: UpdatingStatus = UpdatingStatus.INITIAL
)
class ProfileScreenViewModel(
    private val apiRepository: ApiRepository,
    private val dsRepository: DSRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(value = ProfileScreenUiState())
    val uiState: StateFlow<ProfileScreenUiState> = _uiState.asStateFlow()

    var updatedUserDetails by mutableStateOf(ProfileDetails())

    fun loadUserDetails() {
        viewModelScope.launch {
            dsRepository.dsUserModel.collect() {dsUserModel ->
                _uiState.update {
                    it.copy(
                        userDetails = dsUserModel.toLoggedInUserData(),
                    )
                }
            }
            val (firstName, lastName) = _uiState.value.userDetails.userName.split(" ", limit = 2)
            _uiState.update {
                it.copy(
                    updatedUserDetails = updatedUserDetails.copy(
                        fname = firstName,
                        lname = lastName
                    )
                )
            }
        }
    }

    fun updateUserFirstName(firstName: String) {
        updatedUserDetails = updatedUserDetails.copy(
            fname = firstName
        )
        _uiState.update {
            it.copy(
                updatedUserDetails = updatedUserDetails,
                enableUpdateButton = verifyField()
            )
        }
    }

    fun updateUserLastName(lastName: String) {
        updatedUserDetails = updatedUserDetails.copy(
            lname = lastName
        )
        _uiState.update {
            it.copy(
                updatedUserDetails = updatedUserDetails,
                enableUpdateButton = verifyField()
            )
        }
    }

    fun updateUserEmail(email: String) {
        updatedUserDetails = updatedUserDetails.copy(
            email = email
        )
        _uiState.update {
            it.copy(
                updatedUserDetails = updatedUserDetails,
                enableUpdateButton = verifyField()
            )
        }
    }

    fun updateUserPassword(password: String) {
        updatedUserDetails = updatedUserDetails.copy(
            password = password
        )
        _uiState.update {
            it.copy(
                updatedUserDetails = updatedUserDetails,
                enableUpdateButton = verifyField()
            )
        }
    }

    fun updateUserPhoneNumber(phoneNumber: String) {
        updatedUserDetails = updatedUserDetails.copy(
            phoneNumber = phoneNumber
        )
        _uiState.update {
            it.copy(
                updatedUserDetails = updatedUserDetails,
                enableUpdateButton = verifyField()
            )
        }
    }

    fun uploadUpdatedProfile() {
        _uiState.update {
            it.copy(
                updatingStatus = UpdatingStatus.LOADING
            )
        }
        val profile = ProfileUpdateRequestBody(
            email = updatedUserDetails.email,
            phoneNumber = updatedUserDetails.phoneNumber,
            password = _uiState.value.userDetails.password,
            fname = updatedUserDetails.fname,
            lname = updatedUserDetails.lname
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
                        userName = "${updatedUserDetails.fname} ${updatedUserDetails.lname}",
                        phoneNumber = updatedUserDetails.phoneNumber,
                        email = updatedUserDetails.email,
                        password = updatedUserDetails.password,
                        token = _uiState.value.userDetails.token
                    )
                    dsRepository.saveUserData(dsUserModel)
                    _uiState.update {
                        it.copy(
                            updatingStatus = UpdatingStatus.SUCCESS
                        )
                    }
                } else {
                    Log.i("FAILED_TO_UPDATE_USER_PROFILE", response.toString())
                    _uiState.update {
                        it.copy(
                            updatingStatus = UpdatingStatus.FAILURE
                        )
                    }
                }
            } catch (e: Exception) {
                Log.i("FAILED_TO_UPDATE_USER_PROFILE", e.toString())
                _uiState.update {
                    it.copy(
                        updatingStatus = UpdatingStatus.FAILURE
                    )
                }
            }
        }
    }

    fun uploadProfilePicture(image: Uri, context: Context) {

    }

    fun resetProfileUpdateState() {
        _uiState.update {
            it.copy(
                updatingStatus = UpdatingStatus.INITIAL
            )
        }
    }

    fun changeScreen() {
        _uiState.update {
            it.copy(
                showUpdateProfileScreen = !(_uiState.value.showUpdateProfileScreen)
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            dsRepository.deletePreferences()
        }
    }

    fun verifyField(): Boolean {
        return "${updatedUserDetails.fname } ${updatedUserDetails.lname}" != _uiState.value.userDetails.userName ||
                updatedUserDetails.email != _uiState.value.userDetails.email ||
                updatedUserDetails.phoneNumber != _uiState.value.userDetails.phoneNumber
    }

    init {
        loadUserDetails()
    }
}