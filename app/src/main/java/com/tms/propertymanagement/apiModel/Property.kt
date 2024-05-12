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
    val approved: Boolean,
    val paid: Boolean,
    val postedDate: String,
    val deletionTime: String?,
    val features: List<String>,
    val location: PropertyLocation,
    val paymentDetails: PaymentDetails,
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
    val address: String = "",
    val county: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
@Serializable
data class PropertyImage(
    val id: Int,
    val name: String,
    val approved: Boolean,
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

@Serializable
data class DeletePropertyResponseBody(
    val statusCode: Int,
    val message: String
)

@Serializable
data class PaymentDetails(
    val id: Int,
    val partnerReferenceID: String?,
    val transactionID: String?,
    val msisdn: String?,
    val partnerTransactionID: String?,
    val payerTransactionID: String?,
    val receiptNumber: String?,
    val transactionAmount: Double?,
    val transactionStatus: String,
    val paymentComplete: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)



