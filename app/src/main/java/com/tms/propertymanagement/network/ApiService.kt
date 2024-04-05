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
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // login user
    @POST("api/auth/login")
    suspend fun loginUser(
        @Body userLoginRequestBody: UserLoginRequestBody
    ): Response<UserLoginResponseBody>

    // register user
    @POST("api/auth/register")
    suspend fun registerUser(
        @Body userRegistrationRequestBody: UserRegistrationRequestBody
    ): Response<UserRegistrationResponseBody>

    // fetch categorized properties
    @GET("api/property/categoryId={categoryId}")
    suspend fun fetchCategorizedProperties(
//        @Header("Authorization") token: String,
        @Path("categoryId") categoryId: String
    ): Response<PropertyResponseBody>

    // fetch categories
    @GET("api/category")
    suspend fun fetchCategories(
//        @Header("Authorization") token: String,
    ): Response<CategoryResponseBody>

    // fetch filtered properties

    @GET("api/property/filter")
    suspend fun fetchFilteredProperties(
//        @Header("Authorization") token: String,
        @Query("location") location: String,
        @Query("rooms") rooms: String,
        @Query("categoryId") categoryId: String
    ): Response<PropertyResponseBody>

    // fetch specific property
    @GET("api/property/propertyId={propertyId}")
    suspend fun fetchSpecificProperty(
        @Header("Authorization") token: String,
        @Path("propertyId") propertyId: String
    ): Response<SpecificPropertyResponseBody>

    // upload property
    @Multipart
    @POST("api/property/userId={userId}/create")
    suspend fun uploadProperty(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int,
        @Part("data") property: PropertyUploadRequestBody,
        @Part imageFiles: List<MultipartBody.Part>
    ): Response<PropertyUploadResponseBody>

    // fetch user properties
    @GET("api/property/userId={userId}")
    suspend fun fetchUserProperties(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<PropertyResponseBody>

    // upload property images
    @Multipart
    @POST("api/property/propertyId={propertyId}/image")
    suspend fun uploadPropertyImages(
        @Header("Authorization") token: String,
        @Path("propertyId") propertyId: String,
        @Part imageFiles: List<MultipartBody.Part>
    ): Response<PropertyUploadResponseBody>

    // delete property image
    @DELETE("api/property/propertyId={propertyId}/image/imageId={imageId}")
    suspend fun deletePropertyImage(
        @Header("Authorization") token: String,
        @Path("propertyId") propertyId: String,
        @Path("imageId") imageId: String,
    ): Response<PropertyUploadResponseBody>

    // update property

    @PUT("api/property/propertyId={propertyId}/update")
    suspend fun updateProperty(
        @Header("Authorization") token: String,
        @Body property: PropertyUploadRequestBody,
        @Path("propertyId") propertyId: String
    ): Response<PropertyUploadResponseBody>
}