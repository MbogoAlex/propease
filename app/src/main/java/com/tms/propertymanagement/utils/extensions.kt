package com.tms.propertymanagement.utils

import androidx.room.Embedded
import androidx.room.Relation
import com.propertymanagement.tms.apiModel.Category
import com.propertymanagement.tms.apiModel.PropertyData
import com.propertymanagement.tms.apiModel.PropertyImage
import com.propertymanagement.tms.apiModel.PropertyLocation
import com.propertymanagement.tms.apiModel.PropertyOwner
import com.tms.propertymanagement.db.Feature
import com.tms.propertymanagement.db.Location
import com.tms.propertymanagement.db.Owner
import com.tms.propertymanagement.db.Property
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

fun Property.toPropertyData(
    property: Property,
    category: com.tms.propertymanagement.db.Category,
    owner: Owner,
    features: List<Feature>,
    location: Location
): PropertyData = PropertyData(
    user = owner.toPropertyOwner(),
    propertyId = property.propertyId,
    title = property.title,
    description = property.description,
    category = category.name,
    rooms = property.rooms,
    price = property.price,
    postedDate = property.postedDate,
    features = features.map { it.name },
    location = location.toPropertyLocation(),
    images = emptyList()
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
    images = emptyList(),
)




