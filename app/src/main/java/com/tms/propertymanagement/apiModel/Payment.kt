package com.tms.propertymanagement.apiModel

import kotlinx.serialization.Serializable

@Serializable
data class PaymentRequestBody(
    val propertyId: Int,
    val msisdn: String,
    val transactionAmount: Double
)

@Serializable
data class PaymentResponseBody(
    val data: PaymentDt,
    val message: String,
    val httpStatus: Double,
    val success: Boolean
)

@Serializable
data class PaymentDt(
    val data: PaymentData,
    val message: String,
    val httpStatus: Double,
    val success: Boolean
)

@Serializable
data class PaymentData(
    val partnerReferenceID: String,
    val transactionID: String,
    val message: String,
    val statusCode: String,
)

@Serializable
data class PaymentStatusResponseBody(
    val id: Int,
    val partnerReferenceID: String,
    val transactionID: String,
    val msisdn: String,
    val partnerTransactionID: String?,
    val payerTransactionID: String?,
    val receiptNumber: String?,
    val transactionAmount: Double,
    val transactionStatus: String,
    val paymentComplete: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)