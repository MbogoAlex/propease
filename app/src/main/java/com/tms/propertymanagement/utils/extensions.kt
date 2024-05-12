package com.tms.propertymanagement.utils

import com.propertymanagement.tms.apiModel.Category
import com.propertymanagement.tms.apiModel.PropertyData
import com.propertymanagement.tms.apiModel.PropertyLocation
import com.propertymanagement.tms.apiModel.PropertyOwner
import com.tms.propertymanagement.db.Location
import com.tms.propertymanagement.db.Owner
import com.tms.propertymanagement.db.PaymentDetails
import com.tms.propertymanagement.db.PropertyDetails

fun com.tms.propertymanagement.db.Category.toCategory(): Category = Category(
    id = categoryId,
    name = name
)


fun Owner.toPropertyOwner(): PropertyOwner = PropertyOwner(
    fname = firstName,
    lname = lastName,
    userId = ownerId,
    email = email,
    phoneNumber = phoneNumber,
    profilePic = "",
)

fun Location.toPropertyLocation(): PropertyLocation = PropertyLocation(
    address = address,
    county = county,
    latitude = latitude,
    longitude = longitude
)

fun PaymentDetails.toPropertyPaymentDetails(): com.propertymanagement.tms.apiModel.PaymentDetails = com.propertymanagement.tms.apiModel.PaymentDetails(
    id = paymentId,
    partnerReferenceID = partnerReferenceID,
    transactionID = transactionID,
    msisdn = msisdn,
    partnerTransactionID = partnerTransactionID,
    payerTransactionID = payerTransactionID,
    receiptNumber = receiptNumber,
    transactionAmount = transactionAmount,
    transactionStatus = transactionStatus,
    paymentComplete = paymentComplete,
    createdAt = createdAt,
    updatedAt = updatedAt
)


fun PropertyDetails.toPropertyData(propertyDetails: PropertyDetails): PropertyData = PropertyData(
    user = owner.toPropertyOwner(),
    propertyId = propertyDetails.property.propertyId,
    title = propertyDetails.property.title,
    description = propertyDetails.property.description,
    category = propertyDetails.category.name,
    rooms = propertyDetails.property.rooms,
    price = propertyDetails.property.price,
    postedDate = propertyDetails.property.postedDate,
    features = propertyDetails.features.map { it.name },
    location = PropertyLocation().takeIf { location == null } ?: location!!.toPropertyLocation(),
    approved = propertyDetails.property.approved,
    paid = propertyDetails.property.paid,
    deletionTime = propertyDetails.property.deletionTime,
    paymentDetails = propertyDetails.paymentDetails.toPropertyPaymentDetails(),
    images = emptyList(),
)




