package com.tms.propertymanagement.apiModel

import kotlinx.serialization.Serializable

// load categorized properties
@Serializable
data class PropertyResponseBody(
    val statusCode: Int,
    val message: String,
    val data: PropertyResponseBodyData
)
@Serializable
data class PropertyResponseBodyData(
    val properties: List<CategorizedProperty>
)
@Serializable
data class CategorizedProperty(
    val user: PropertyOwner,
    val propertyId: Int,
    val title: String,
    val description: String,
    val category: String,
    val rooms: Int,
    val price: Double,
    val availableDate: String,
    val features: List<String>,
    val location: PropertyLocation,
    val images: List<PropertyImage>
)
@Serializable
data class PropertyOwner(
    val userId: Int,
    val email: String,
    val phoneNumber: String,
    val profilePic: String,
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


