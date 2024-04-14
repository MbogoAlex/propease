package com.propertymanagement.tms.network

import com.propertymanagement.tms.apiModel.CategoryResponseBody
import com.propertymanagement.tms.apiModel.DeletePropertyResponseBody
import com.propertymanagement.tms.apiModel.ProfileUpdateRequestBody
import com.propertymanagement.tms.apiModel.ProfileUpdateResponseBody
import com.propertymanagement.tms.apiModel.PropertyResponseBody
import com.propertymanagement.tms.apiModel.PropertyUploadRequestBody
import com.propertymanagement.tms.apiModel.PropertyUploadResponseBody
import com.propertymanagement.tms.apiModel.SpecificPropertyResponseBody
import com.propertymanagement.tms.apiModel.UserLoginRequestBody
import com.propertymanagement.tms.apiModel.UserLoginResponseBody
import com.propertymanagement.tms.apiModel.UserRegistrationRequestBody
import com.propertymanagement.tms.apiModel.UserRegistrationResponseBody
import okhttp3.MultipartBody
import retrofit2.Response

interface ApiRepository {
    suspend fun loginUser(userLoginRequestBody: UserLoginRequestBody): Response<UserLoginResponseBody>
    suspend fun registerUser(userRegistrationRequestBody: UserRegistrationRequestBody): Response<UserRegistrationResponseBody>

    suspend fun fetchCategories(
//        token: String
    ): Response<CategoryResponseBody>

    suspend fun fetchFilteredProperties(
//        token: String,
        location: String,
        rooms: String,
        categoryId: String
    ): Response<PropertyResponseBody>
    suspend fun fetchSpecificProperty(
        propertyId: String
    ): Response<SpecificPropertyResponseBody>
    suspend fun uploadProperty(
        token: String,
        userId: Int,
        property: PropertyUploadRequestBody,
        images: List<MultipartBody.Part>
    ): Response<PropertyUploadResponseBody>

    suspend fun fetchUserProperties(
        token: String,
        userId: Int
    ): Response<PropertyResponseBody>
    suspend fun uploadPropertyImages(token: String, propertyId: String, images: List<MultipartBody.Part>): Response<PropertyUploadResponseBody>

    suspend fun deletePropertyImage(token: String, propertyId: String, imageId: String): Response<PropertyUploadResponseBody>

    suspend fun updateProperty(token: String, property: PropertyUploadRequestBody, propertyId: String): Response<PropertyUploadResponseBody>

    suspend fun updateUserProfile(
        token: String,
        userId: String,
        profileDetails: ProfileUpdateRequestBody
    ): Response<ProfileUpdateResponseBody>

    suspend fun deleteProperty(
        token: String,
        propertyId: Int
    ): Response<DeletePropertyResponseBody>
}

class NetworkApiRepository(private val apiService: ApiService): ApiRepository {
    override suspend fun loginUser(userLoginRequestBody: UserLoginRequestBody): Response<UserLoginResponseBody> = apiService.loginUser(userLoginRequestBody)
    override suspend fun registerUser(userRegistrationRequestBody: UserRegistrationRequestBody): Response<UserRegistrationResponseBody> = apiService.registerUser(userRegistrationRequestBody)

    override suspend fun fetchCategories(): Response<CategoryResponseBody> = apiService.fetchCategories(
//        token = "Bearer $token"
    )

    override suspend fun fetchFilteredProperties(
//        token: String,
        location: String,
        rooms: String,
        categoryId: String
    ): Response<PropertyResponseBody> = apiService.fetchFilteredProperties(
//        token = "Bearer $token",
        location = location,
        rooms = rooms,
        categoryId = categoryId
    )

    override suspend fun fetchSpecificProperty(
        propertyId: String
    ): Response<SpecificPropertyResponseBody> = apiService.fetchSpecificProperty(
        propertyId = propertyId
    )

    override suspend fun uploadProperty(
        token: String,
        userId: Int,
        property: PropertyUploadRequestBody,
        images: List<MultipartBody.Part>,
    ): Response<PropertyUploadResponseBody> = apiService.uploadProperty(
        token = "Bearer $token",
        userId = userId,
        property = property,
        imageFiles = images
    )

    override suspend fun fetchUserProperties(
        token: String,
        userId: Int
    ): Response<PropertyResponseBody> = apiService.fetchUserProperties(
        token = "Bearer $token",
        userId = userId
    )

    override suspend fun uploadPropertyImages(
        token: String,
        propertyId: String,
        images: List<MultipartBody.Part>
    ): Response<PropertyUploadResponseBody> = apiService.uploadPropertyImages(
        token = "Bearer $token",
        propertyId = propertyId,
        imageFiles = images
    )

    override suspend fun deletePropertyImage(
        token: String,
        propertyId: String,
        imageId: String
    ): Response<PropertyUploadResponseBody> = apiService.deletePropertyImage(
        token = "Bearer $token",
        propertyId = propertyId,
        imageId = imageId
    )

    override suspend fun updateProperty(
        token: String,
        property: PropertyUploadRequestBody,
        propertyId: String
    ): Response<PropertyUploadResponseBody> = apiService.updateProperty(
        token = "Bearer $token",
        property = property,
        propertyId = propertyId
    )

    override suspend fun updateUserProfile(
        token: String,
        userId: String,
        profileDetails: ProfileUpdateRequestBody
    ): Response<ProfileUpdateResponseBody> = apiService.updateUserProfile(
        token = "Bearer $token",
        userId = userId,
        profileDetails = profileDetails
    )

    override suspend fun deleteProperty(
        token: String,
        propertyId: Int
    ): Response<DeletePropertyResponseBody> = apiService.deleteProperty(
        token = "Bearer $token",
        propertyId = propertyId
    )

}