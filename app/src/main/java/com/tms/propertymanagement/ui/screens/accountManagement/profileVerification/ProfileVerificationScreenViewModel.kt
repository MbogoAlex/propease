package com.tms.propertymanagement.ui.screens.accountManagement.profileVerification

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanagement.tms.network.ApiRepository
import com.propertymanagement.tms.propEaseDataStore.DSRepository
import com.propertymanagement.tms.propEaseDataStore.DSUserModel
import com.propertymanagement.tms.utils.ReusableFunctions
import com.propertymanagement.tms.utils.ReusableFunctions.toLoggedInUserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.FileInputStream

data class ProfileVerificationScreenUiState(
    val frontImage: Uri? = null,
    val backImage: Uri? = null,
    val documents: List<Uri> = emptyList(),
    val userData: ReusableFunctions.LoggedInUserData = ReusableFunctions.LoggedInUserData(),
    val saveButtonEnabled: Boolean = false,
    val executionStatus: ReusableFunctions.ExecutionStatus = ReusableFunctions.ExecutionStatus.INITIAL,
)
class ProfileVerificationScreenViewModel(
    private val apiRepository: ApiRepository,
    private val dsRepository: DSRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(value = ProfileVerificationScreenUiState())
    val uiState: StateFlow<ProfileVerificationScreenUiState> = _uiState.asStateFlow()

    val documents = mutableListOf<Uri>()

    fun loadStartUpDetails() {
        viewModelScope.launch {
            dsRepository.dsUserModel.collect() {dsUserModel ->
                _uiState.update {
                    it.copy(
                        userData = dsUserModel.toLoggedInUserData()
                    )
                }
            }
        }
    }

    fun uploadFrontPart(image: Uri?) {
        _uiState.update {
            it.copy(
                frontImage = image,
            )
        }
    }

    fun uploadBackPart(image: Uri?) {
        _uiState.update {
            it.copy(
                backImage = image,
            )
        }
    }

    fun uploadDocuments(context: Context) {
        _uiState.update {
            it.copy(
                executionStatus = ReusableFunctions.ExecutionStatus.LOADING
            )
        }
        documents.add(_uiState.value.frontImage!!)
        documents.add(_uiState.value.backImage!!)

        var imageParts = ArrayList<MultipartBody.Part>()
        documents.forEach { uri ->
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r", null)
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
                val imagePart = MultipartBody.Part.createFormData("files", "upload.$extension", requestFile)
                imageParts.add(imagePart)
            }
        }

        viewModelScope.launch {
            try {
                val response = apiRepository.uploadUserDocuments(
                    token = _uiState.value.userData.token,
                    userId = _uiState.value.userData.userId!!,
                    files = imageParts
                )

                if(response.isSuccessful) {
                    val dsUserModel = DSUserModel(
                        userId = _uiState.value.userData.userId,
                        userName = _uiState.value.userData.userName,
                        phoneNumber = _uiState.value.userData.phoneNumber,
                        email = _uiState.value.userData.email,
                        password = _uiState.value.userData.password,
                        token = _uiState.value.userData.token,
                        approvalStatus = response.body()?.data?.profile?.user?.approvalStatus!!
                    )
                    dsRepository.saveUserData(dsUserModel)
                    _uiState.update {
                        it.copy(
                            executionStatus = ReusableFunctions.ExecutionStatus.SUCCESS
                        )
                    }
                    Log.i("DOCUMENTS_UPLOAD_SECCESS", "DOCUMENTS UPLOADED")
                } else {
                    _uiState.update {
                        it.copy(
                            executionStatus = ReusableFunctions.ExecutionStatus.FAIL
                        )
                    }
                    Log.e("DOCUMENTS_UPLOAD_FAIL_RESPONSE", response.toString())
                }

            }catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        executionStatus = ReusableFunctions.ExecutionStatus.FAIL
                    )
                }
                Log.e("DOCUMENTS_UPLOAD_FAIL_EXCEPTION", e.toString())
            }



        }
    }

    fun resetUploadingStatus() {
        _uiState.update {
            it.copy(
                executionStatus = ReusableFunctions.ExecutionStatus.INITIAL
            )
        }
    }

    fun checkIfAllFieldsAreFilled(): Boolean {
        _uiState.update {
            it.copy(
                saveButtonEnabled = _uiState.value.frontImage != null &&
                        _uiState.value.backImage != null
            )
        }
        return _uiState.value.frontImage != null &&
                _uiState.value.backImage != null
    }

    init {
        loadStartUpDetails()
    }
}