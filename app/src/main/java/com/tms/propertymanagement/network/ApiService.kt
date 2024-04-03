package com.tms.propertymanagement.network

import com.tms.propertymanagement.apiModel.UserLoginRequestBody
import com.tms.propertymanagement.apiModel.UserLoginResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    // login user
    @POST("api/auth/login")
    suspend fun loginUser(
        @Body userLoginRequestBody: UserLoginRequestBody
    ): Response<UserLoginResponseBody>
}