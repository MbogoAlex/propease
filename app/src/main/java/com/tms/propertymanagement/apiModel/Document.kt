package com.tms.propertymanagement.apiModel

import com.propertymanagement.tms.apiModel.Role
import kotlinx.serialization.Serializable

@Serializable
data class DocumentUploadResponseBody(
    val statusCode: Int,
    val message: String,
    val data: ProfileDt
)
@Serializable
data class ProfileDt(
    val profile: UserProfile
)
@Serializable
data class UserProfile(
    val documents: List<Document>,
    val user: UserDt
)
@Serializable
data class Document(
    val id: Int,
    val name: String
)
@Serializable
data class UserDt(
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

