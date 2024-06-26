package com.propertymanagement.tms.ui.screens.accountManagement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanagement.tms.apiModel.UserRegistrationRequestBody
import com.propertymanagement.tms.network.ApiRepository
import com.propertymanagement.tms.propEaseDataStore.DSRepository
import com.propertymanagement.tms.utils.ReusableFunctions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class RegistrationStatus {
    INITIAL,
    LOADING,
    SUCCESS,
    FAIL
}
data class RegistrationScreenUIState(
    val registrationDetails: RegistrationDetails = RegistrationDetails(),
    val registrationButtonEnabled: Boolean = false,
    val registrationStatus: RegistrationStatus = RegistrationStatus.INITIAL,
    val registrationResponseMessage: String = ""
)

data class RegistrationDetails(
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val password: String = ""
)
class RegistrationScreenViewModel(
    private val apiRepository: ApiRepository,
    private val dsRepository: DSRepository,
): ViewModel() {
    private val _uiState = MutableStateFlow(value = RegistrationScreenUIState())
    val uiState: StateFlow<RegistrationScreenUIState> = _uiState.asStateFlow()

    var registrationDetails by mutableStateOf(
        RegistrationDetails()
    )

    fun updateFirstName(firstName: String) {
        registrationDetails = registrationDetails.copy(
            firstName = firstName
        )
        _uiState.update {
            it.copy(
                registrationDetails = registrationDetails,
                registrationButtonEnabled = allFieldsFilled()
            )
        }
    }

    fun updateLastName(lastName: String) {
        registrationDetails = registrationDetails.copy(
            lastName = lastName
        )
        _uiState.update {
            it.copy(
                registrationDetails = registrationDetails,
                registrationButtonEnabled = allFieldsFilled()
            )
        }
    }

    fun updateEmail(email: String) {
        registrationDetails = registrationDetails.copy(
            email = email
        )
        _uiState.update {
            it.copy(
                registrationDetails = registrationDetails,
                registrationButtonEnabled = allFieldsFilled()
            )
        }
    }

    fun updatePhoneNumber(phoneNumber: String) {
        registrationDetails = registrationDetails.copy(
            phoneNumber = phoneNumber
        )
        _uiState.update {
            it.copy(
                registrationDetails = registrationDetails,
                registrationButtonEnabled = allFieldsFilled()
            )
        }
    }

    fun updatePassword(password: String) {
        registrationDetails = registrationDetails.copy(
            password = password
        )
        _uiState.update {
            it.copy(
                registrationDetails = registrationDetails,
                registrationButtonEnabled = allFieldsFilled()
            )
        }
    }

    fun allFieldsFilled(): Boolean {
        return registrationDetails.firstName.isNotEmpty() &&
                registrationDetails.lastName.isNotEmpty() &&
                registrationDetails.email.isNotEmpty() &&
                registrationDetails.password.isNotEmpty() &&
                registrationDetails.phoneNumber.isNotEmpty()
    }

    fun registerUser() {
        if(ReusableFunctions.checkIfEmailIsValid(registrationDetails.email)) {
            _uiState.update {
                it.copy(
                    registrationStatus = RegistrationStatus.LOADING,
                    registrationButtonEnabled = false
                )
            }
            val userRegistrationRequestBody = UserRegistrationRequestBody(
                fname = registrationDetails.firstName,
                lname = registrationDetails.lastName,
                email = registrationDetails.email,
                phoneNumber = registrationDetails.phoneNumber,
                password = registrationDetails.password
            )
            viewModelScope.launch {
                try {
                    val response = apiRepository.registerUser(userRegistrationRequestBody)
                    if(response.isSuccessful) {
                        _uiState.update {
                            it.copy(
                                registrationStatus = RegistrationStatus.SUCCESS,
                                registrationResponseMessage = "Registration success"
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                registrationStatus = RegistrationStatus.FAIL,
                                registrationResponseMessage = "Email or Phone Number already taken"
                            )
                        }
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            registrationStatus = RegistrationStatus.FAIL,
                            registrationResponseMessage = "Failed to register. Try again later"
                        )
                    }
                }
            }
        }

    }

    fun enableButton() {
        _uiState.update {
            it.copy(
                registrationButtonEnabled = true
            )
        }
    }

    fun resetRegistrationStatus() {
        _uiState.update {
            it.copy(
                registrationStatus = RegistrationStatus.INITIAL
            )
        }
    }



}