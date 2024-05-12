package com.propertymanagement.tms.network

import com.propertymanagement.tms.apiModel.CategoryResponseBody
import com.propertymanagement.tms.apiModel.DeletePropertyResponseBody
import com.propertymanagement.tms.apiModel.ProfileUpdateRequestBody
import com.propertymanagement.tms.apiModel.ProfileUpdateResponseBody
import com.propertymanagement.tms.apiModel.PropertyResponseBody
import com.propertymanagement.tms.apiModel.PropertyUploadRequestBody
import com.propertymanagement.tms.apiModel.PropertyUploadResponseBody
import com.propertymanagement.tms.apiModel.SpecificPropertyResponseBody
import com.propertymanagement.tms.apiModel.UserDetailsResponseBody
import com.propertymanagement.tms.apiModel.UserLoginRequestBody
import com.propertymanagement.tms.apiModel.UserLoginResponseBody
import com.propertymanagement.tms.apiModel.UserRegistrationRequestBody
import com.propertymanagement.tms.apiModel.UserRegistrationResponseBody
import com.tms.propertymanagement.apiModel.DocumentUploadResponseBody
import com.tms.propertymanagement.apiModel.PaymentRequestBody
import com.tms.propertymanagement.apiModel.PaymentResponseBody
import com.tms.propertymanagement.apiModel.PaymentStatusResponseBody
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

    // update user profile
    @PUT("api/profile/userId={userId}/update")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
        @Body profileDetails: ProfileUpdateRequestBody
    ): Response<ProfileUpdateResponseBody>

    // delete property
    @DELETE("api/property/propertyId={propertyId}")
    suspend fun deleteProperty(
        @Header("Authorization") token: String,
        @Path("propertyId") propertyId: Int
    ) : Response<DeletePropertyResponseBody>

    //
    @GET("api/profile/userId={userId}/user")
    suspend fun getUser(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<UserDetailsResponseBody>

    @Multipart
    @POST("api/profile/userId={userId}/upload")
    suspend fun uploadUserDocuments(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int,
        @Part files: List<MultipartBody.Part>
    ): Response<DocumentUploadResponseBody>
    @POST("api/payment/initialize")
    suspend fun payForPropertyAd(
        @Header("Authorization") token: String,
        @Body paymentRequestBody: PaymentRequestBody
    ): Response<PaymentResponseBody>

    @GET("api/payment/partnerTransactionID={partnerTransactionID}/status")
    suspend fun getPaymentStatus(
        @Header("Authorization") token: String,
        @Path("partnerTransactionID") partnerTransactionID: String
    ): Response<PaymentStatusResponseBody>

}