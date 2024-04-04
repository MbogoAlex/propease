package com.tms.propertymanagement.network

import com.tms.propertymanagement.apiModel.CategoryResponseBody
import com.tms.propertymanagement.apiModel.PropertyResponseBody
import com.tms.propertymanagement.apiModel.UserLoginRequestBody
import com.tms.propertymanagement.apiModel.UserLoginResponseBody
import com.tms.propertymanagement.apiModel.UserRegistrationRequestBody
import com.tms.propertymanagement.apiModel.UserRegistrationResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
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
        @Header("Authorization") token: String,
        @Path("categoryId") categoryId: String
    ): Response<PropertyResponseBody>

    // fetch categories
    @GET("api/category")
    suspend fun fetchCategories(
        @Header("Authorization") token: String,
    ): Response<CategoryResponseBody>

    // fetch filtered properties

    @GET("api/property/filter")
    suspend fun fetchFilteredProperties(
        @Header("Authorization") token: String,
        @Query("location") location: String,
        @Query("rooms") rooms: String,
        @Query("categoryId") categoryId: String
    ): Response<PropertyResponseBody>
}