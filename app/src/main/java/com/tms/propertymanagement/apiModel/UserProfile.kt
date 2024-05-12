package com.propertymanagement.tms.apiModel

import kotlinx.serialization.Serializable

@Serializable
data class ProfileUpdateRequestBody(
    val email: String,
    val phoneNumber: String,
    val password: String,
    val fname: String,
    val lname: String
)
@Serializable
data class ProfileUpdateResponseBody(
    val statusCode: Int,
    val message: String,
    val data: User
)
@Serializable
data class User (
    val user: UserData
)
@Serializable
data class UserData(
    val id: Int,
    val email: String,
    val phoneNumber: String,
    val imageUrl: String?,
    val fname: String,
    val lname: String
)

@Serializable
data class UserDetailsResponseBody(
    val statusCode: Int,
    val message: String,
    val data: UserProfileData
)

@Serializable
data class UserProfileData(
    val profiles: ProfileData
)

@Serializable
data class ProfileData(
    val id: Int,
    val email: String,
    val phoneNumber: String,
    val imageUrl: String?,
    val approvalStatus: String,
    val approved: Boolean,
    val roles: List<Role>,
    val fname: String,
    val lname: String
)

/*
update profile
http://192.168.47.6:8080/pManager/api/profile/userId=1/update
 */