package com.tms.propertymanagement.network

import com.tms.propertymanagement.apiModel.CategoryResponseBody
import com.tms.propertymanagement.apiModel.PropertyResponseBody
import com.tms.propertymanagement.apiModel.PropertyUploadRequestBody
import com.tms.propertymanagement.apiModel.PropertyUploadResponseBody
import com.tms.propertymanagement.apiModel.SpecificPropertyResponseBody
import com.tms.propertymanagement.apiModel.UserLoginRequestBody
import com.tms.propertymanagement.apiModel.UserLoginResponseBody
import com.tms.propertymanagement.apiModel.UserRegistrationRequestBody
import com.tms.propertymanagement.apiModel.UserRegistrationResponseBody
import okhttp3.MultipartBody
import retrofit2.Response

interface ApiRepository {
    suspend fun loginUser(userLoginRequestBody: UserLoginRequestBody): Response<UserLoginResponseBody>
    suspend fun registerUser(userRegistrationRequestBody: UserRegistrationRequestBody): Response<UserRegistrationResponseBody>

    suspend fun fetchCategories(token: String): Response<CategoryResponseBody>

    suspend fun fetchFilteredProperties(token: String, location: String, rooms: String, categoryId: String): Response<PropertyResponseBody>
    suspend fun fetchSpecificProperty(token: String, propertyId: String): Response<SpecificPropertyResponseBody>
    suspend fun uploadProperty(
        token: String,
        userId: Int,
        property: PropertyUploadRequestBody,
        images: List<MultipartBody.Part>
    ): Response<PropertyUploadResponseBody>
}

class NetworkApiRepository(private val apiService: ApiService): ApiRepository {
    override suspend fun loginUser(userLoginRequestBody: UserLoginRequestBody): Response<UserLoginResponseBody> = apiService.loginUser(userLoginRequestBody)
    override suspend fun registerUser(userRegistrationRequestBody: UserRegistrationRequestBody): Response<UserRegistrationResponseBody> = apiService.registerUser(userRegistrationRequestBody)

    override suspend fun fetchCategories(token: String): Response<CategoryResponseBody> = apiService.fetchCategories(
        token = "Bearer $token"
    )

    override suspend fun fetchFilteredProperties(
        token: String,
        location: String,
        rooms: String,
        categoryId: String
    ): Response<PropertyResponseBody> = apiService.fetchFilteredProperties(
        token = "Bearer $token",
        location = location,
        rooms = rooms,
        categoryId = categoryId
    )

    override suspend fun fetchSpecificProperty(token: String, propertyId: String): Response<SpecificPropertyResponseBody> = apiService.fetchSpecificProperty(
        token = "Bearer $token",
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

}