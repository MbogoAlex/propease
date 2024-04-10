package com.propertymanagement.tms.ui.screens.accountManagement

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanagement.tms.apiModel.UserLoginRequestBody
import com.propertymanagement.tms.network.ApiRepository
import com.propertymanagement.tms.propEaseDataStore.DSRepository
import com.propertymanagement.tms.propEaseDataStore.DSUserModel
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
    private val dsRepository: DSRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _uiState = MutableStateFlow(value = LoginScreenUiState())
    val uiState: StateFlow<LoginScreenUiState> = _uiState.asStateFlow()

    private val phoneNumber: String? = savedStateHandle[LoginScreenDestination.phoneNumber]
    private val password: String? = savedStateHandle[LoginScreenDestination.password]


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
                loginStatus = LoginStatus.LOADING,
                loginButtonEnabled = false
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
                        userName = "${response.body()?.data?.user?.userInfo?.fname!!} ${response.body()?.data?.user?.userInfo?.lname!!}",
                        phoneNumber = response.body()?.data?.user?.userInfo?.phoneNumber!!,
                        email = response.body()?.data?.user?.userInfo?.email!!,
                        password = loginDetails.password,
                        token = response.body()?.data?.user?.token!!
                    )

                    dsRepository.saveUserData(dsUserModel)
                    if(response.body()?.data?.user?.userInfo?.imageUrl != null) {
                        dsRepository.saveUserProfilePicture(response.body()?.data?.user?.userInfo?.imageUrl)
                    }


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
                            loginResponseMessage = "Invalid email or password"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loginStatus = LoginStatus.FAILURE,
                        loginResponseMessage = "Failed to login. Try again later"
                    )
                }
            }
        }
    }

    fun enableButton() {
        _uiState.update {
            it.copy(
                loginButtonEnabled = true
            )
        }
    }

    fun resetLoginStatus() {
        _uiState.update {
            it.copy(
                loginStatus = LoginStatus.INITIAL
            )
        }
    }



    fun initializeFields() {
        Log.i("INITIALIZING_FIELDS", "$phoneNumber $password")
        loginDetails = loginDetails.copy(
            phoneNumber = phoneNumber.takeIf { phoneNumber != null } ?: "",
            password = password.takeIf { phoneNumber != null } ?: ""
        )
        _uiState.update {
            it.copy(
                loginDetails = loginDetails,
                loginButtonEnabled = allFieldsFilled(),
            )
        }
    }

    init {
        initializeFields()
    }

}