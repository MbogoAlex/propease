package com.tms.propertymanagement.accountManagement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class LoginStatus{
    INITIAL,
    LOADING,
    SUCCESS,
    FAILURE
}
data class LoginDetails(
    val phoneNumber: String = "",
    val password: String = ""
)

data class LoginScreenUiState(
    val loginDetails: LoginDetails = LoginDetails(),
    val loginButtonEnabled: Boolean = false,
    val loginStatus: LoginStatus = LoginStatus.INITIAL

)
class LoginScreenViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(value = LoginScreenUiState())
    val uiState: StateFlow<LoginScreenUiState> = _uiState.asStateFlow()

    var loginDetails by mutableStateOf(
        LoginDetails()
    )

    fun updatePhoneNumber(phoneNumber: String) {
        loginDetails = loginDetails.copy(
            phoneNumber = phoneNumber
        )
        _uiState.update {
            it.copy(
                loginDetails = loginDetails,
                loginButtonEnabled = allFieldsFilled()
            )
        }
    }

    fun updatePassword(password: String) {
        loginDetails = loginDetails.copy(
            password = password
        )
        _uiState.update {
            it.copy(
                loginDetails = loginDetails,
                loginButtonEnabled = allFieldsFilled()
            )
        }
    }

    fun allFieldsFilled(): Boolean {
        return loginDetails.phoneNumber.isNotEmpty() &&
                loginDetails.password.isNotEmpty()
    }

}