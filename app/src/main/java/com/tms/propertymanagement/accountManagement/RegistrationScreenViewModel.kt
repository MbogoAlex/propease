package com.tms.propertymanagement.accountManagement

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class RegistrationStatus {
    INITIAL,
    LOADING,
    SUCCESS,
    FAIL
}
data class RegistrationScreenUIState(
    val registrationDetails: RegistrationDetails = RegistrationDetails(),
    val registrationButtonEnabled: Boolean = false,
    val registrationStatus: RegistrationStatus = RegistrationStatus.INITIAL
)

data class RegistrationDetails(
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val password: String = ""
)
class RegistrationScreenViewModel: ViewModel() {
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


}