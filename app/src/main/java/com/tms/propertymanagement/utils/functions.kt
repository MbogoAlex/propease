package com.tms.propertymanagement.utils

import com.propertymanagement.tms.apiModel.Category
import com.propertymanagement.tms.apiModel.PaymentDetails
import com.propertymanagement.tms.apiModel.PropertyData
import com.propertymanagement.tms.apiModel.PropertyLocation
import com.propertymanagement.tms.apiModel.PropertyOwner

data class SpecificPropertyData(
    val property: PropertyData
)

val propertyCategoryData = Category(
    id = 0,
    name = ""
)

val propertyOwner: PropertyOwner = PropertyOwner(
    userId = 0,
    email = "",
    phoneNumber = "",
    profilePic = "",
    fname = "",
    lname = "",
)

val propertyLocation = PropertyLocation(
    county = "",
    address = "",
    latitude = 0.0,
    longitude = 0.0
)

val propertyPayment = PaymentDetails(
    id = 1,
    partnerReferenceID = "1234321",
    transactionID = "123432",
    msisdn = "123432",
    partnerTransactionID = "1234321",
    payerTransactionID = "1234321",
    receiptNumber = "1234321",
    transactionAmount = 200.0,
    paymentComplete = true,
    createdAt = 2345432222,
    updatedAt = 1322222233,
    transactionStatus = "PAID"
)

val propertyData = PropertyData(
    user = propertyOwner,
    propertyId = 0,
    title = "",
    description = "",
    category = "",
    rooms = 0,
    price = 0.0,
    approved = false,
    paid = false,
    postedDate = "",
    deletionTime = null,
    features = emptyList(),
    location = propertyLocation,
    paymentDetails = propertyPayment,
    images = emptyList()
)