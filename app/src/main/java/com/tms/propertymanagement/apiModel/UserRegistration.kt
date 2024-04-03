package com.tms.propertymanagement.apiModel

import kotlinx.serialization.Serializable

@Serializable
data class UserRegistrationRequestBody (
    val fname: String,
    val lname: String,
    val email: String,
    val phoneNumber: String,
    val password: String
)
@Serializable
data class UserRegistrationResponseBody (
    val statusCode: Int,
    val message: String,
    val data: UserRegistrationResponseBodyData
)
@Serializable
data class UserRegistrationResponseBodyData (
    val user: UserRegistrationResponseBodyDataUser
)
@Serializable
data class UserRegistrationResponseBodyDataUser (
    val id: Int,
    val email: String,
    val phoneNumber: String,
    val fname: String,
    val lname: String
)