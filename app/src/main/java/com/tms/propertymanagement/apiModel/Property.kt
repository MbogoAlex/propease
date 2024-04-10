package com.propertymanagement.tms.apiModel

import kotlinx.serialization.Serializable

// load filtered properties
@Serializable
data class PropertyResponseBody(
    val statusCode: Int,
    val message: String,
    val data: PropertyResponseBodyData
)
@Serializable
data class PropertyResponseBodyData(
    val properties: List<PropertyData>
)
@Serializable
data class PropertyData(
    val user: PropertyOwner,
    val propertyId: Int,
    val title: String,
    val description: String,
    val category: String,
    val rooms: Int,
    val price: Double,
    val postedDate: String,
    val features: List<String>,
    val location: PropertyLocation,
    val images: List<PropertyImage>
)
@Serializable
data class PropertyOwner(
    val userId: Int,
    val email: String,
    val phoneNumber: String,
    val profilePic: String?,
    val fname: String,
    val lname: String,
)
@Serializable
data class PropertyLocation(
    val address: String,
    val county: String,
    val latitude: Double,
    val longitude: Double
)
@Serializable
data class PropertyImage(
    val id: Int,
    val name: String
)

// fetch specific property
@Serializable
data class SpecificPropertyResponseBody(
    val statusCode: Int,
    val message: String,
    val data: SpecificPropertyResponseBodyData
)
@Serializable
data class SpecificPropertyResponseBodyData(
    val property: PropertyData
)

// property upload
@Serializable
data class PropertyUploadRequestBody (
    val title: String,
    val description: String,
    val categoryId: String,
    val price: Double,
    val rooms: Int,
    val postedDate: String,
    val location: PropertyLocation,
    val features: List<String>
)

@Serializable
data class PropertyUploadResponseBody (
    val statusCode: Int,
    val message: String,
    val data: SpecificPropertyResponseBodyData
)


