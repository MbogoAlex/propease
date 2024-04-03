package com.tms.propertymanagement.accountManagement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tms.propertymanagement.apiModel.UserLoginRequestBody
import com.tms.propertymanagement.network.ApiRepository
import com.tms.propertymanagement.propEaseDataStore.DSRepository
import com.tms.propertymanagement.propEaseDataStore.DSUserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    val loginStatus: LoginStatus = LoginStatus.INITIAL,
    val loginResponseMessage: String = "",

)
class LoginScreenViewModel(
    private val apiRepository: ApiRepository,
    private val dsRepository: DSRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(value = LoginScreenUiState())
    val uiState: StateFlow<LoginScreenUiState> = _uiState.asStateFlow()

    private var loginDetails by mutableStateOf(
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

    fun loginUser() {
        _uiState.update {
            it.copy(
                loginStatus = LoginStatus.LOADING
            )
        }
        val userLoginRequestBody = UserLoginRequestBody(
            username = loginDetails.phoneNumber,
            password = loginDetails.password
        )

        // attempt login

        viewModelScope.launch {
            try {
                val response = apiRepository.loginUser(userLoginRequestBody)
                if(response.isSuccessful) {

                    // save to datastore

                    val dsUserModel = DSUserModel(
                        userId = response.body()?.data?.user?.userInfo?.id!!,
                        userName = "${response.body()?.data?.user?.userInfo?.fname!!} ${response.body()?.data?.user?.userInfo?.mname!!}",
                        phoneNumber = response.body()?.data?.user?.userInfo?.phoneNumber!!,
                        email = response.body()?.data?.user?.userInfo?.email!!,
                        password = loginDetails.password,
                        token = response.body()?.data?.user?.token!!
                    )

                    dsRepository.saveUserData(dsUserModel)

                    _uiState.update {
                        it.copy(
                            loginStatus = LoginStatus.SUCCESS,
                            loginResponseMessage = "Login success"
                        )
                    }

                } else {
                    _uiState.update {
                        it.copy(
                            loginStatus = LoginStatus.FAILURE,
                            loginResponseMessage = response.message()
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loginStatus = LoginStatus.FAILURE,
                        loginResponseMessage = e.message.toString()
                    )
                }
            }
        }
    }

    fun resetLoginStatus() {
        _uiState.update {
            it.copy(
                loginStatus = LoginStatus.INITIAL
            )
        }
    }

}