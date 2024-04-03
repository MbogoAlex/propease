package com.tms.propertymanagement.apiModel

import kotlinx.serialization.Serializable

@Serializable
data class UserLoginRequestBody(
    val username: String,
    val password: String
)
@Serializable
data class UserLoginResponseBody(
    val statusCode: Int,
    val message: String,
    val data: UserLoginResponseBodyData
)
@Serializable
data class UserLoginResponseBodyData(
    val user: UserLoginResponseBodyDataUser
)
@Serializable
data class UserLoginResponseBodyDataUser(
    val userInfo: UserLoginResponseBodyDataUserInfo,
    val token: String
)
@Serializable
data class UserLoginResponseBodyDataUserInfo(
    val id: Int,
    val email: String,
    val phoneNumber: String,
    val imageUrl: String?,
    val fname: String,
    val lname: String
)