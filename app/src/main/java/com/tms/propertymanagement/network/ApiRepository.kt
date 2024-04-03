package com.tms.propertymanagement.network

import com.tms.propertymanagement.apiModel.UserLoginRequestBody
import com.tms.propertymanagement.apiModel.UserLoginResponseBody
import retrofit2.Response

interface ApiRepository {
    suspend fun loginUser(userLoginRequestBody: UserLoginRequestBody): Response<UserLoginResponseBody>
}

class NetworkApiRepository(private val apiService: ApiService): ApiRepository {
    override suspend fun loginUser(userLoginRequestBody: UserLoginRequestBody): Response<UserLoginResponseBody> = apiService.loginUser(userLoginRequestBody)

}