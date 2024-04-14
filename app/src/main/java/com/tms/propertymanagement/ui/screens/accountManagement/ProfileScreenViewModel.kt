package com.propertymanagement.tms.ui.screens.accountManagement

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.FileInputStream

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
    val uploadedPicture: Uri? = null,
    val forcedLogin: Boolean = false,
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

    fun changeProfilePicture(uri: Uri, context: Context) {
        _uiState.update {
            it.copy(
                uploadedPicture = uri,
                updatingStatus = UpdatingStatus.LOADING
            )
        }
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r", null)
        var imagePart: MultipartBody.Part
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
            val imagePartData = MultipartBody.Part.createFormData("file", "upload.$extension", requestFile)
            imagePart = imagePartData
        }
        viewModelScope.launch {
            val response = apiRepository
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