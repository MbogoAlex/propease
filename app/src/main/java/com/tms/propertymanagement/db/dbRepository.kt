package com.tms.propertymanagement.db

import kotlinx.coroutines.flow.Flow

interface DBRepository {
    // insert property details
    suspend fun insertPropertyDetails(
        property: Property,
        owner: Owner,
        category: Category,
        location: Location,
        features: List<Feature>
    )

    // insert feature
    suspend fun insertFeature(feature: Feature)

    // get all properties
    fun getAllProperties(): Flow<List<PropertyDetails>>

    // get specific property
    fun getSpecificProperty(propertyId: Int): Flow<PropertyDetails>

    // insert owner
    suspend fun insertOwner(owner: Owner)

    // insert category
    suspend fun insertCategory(category: Category)

    // insert property

    suspend fun insertProperty(property: Property)

    // insert location
    suspend fun insertLocation(location: Location)
}

class OfflineRepository(private val appDao: AppDao): DBRepository {
    override suspend fun insertPropertyDetails(
        property: Property,
        owner: Owner,
        category: Category,
        location: Location,
        features: List<Feature>
    ) = appDao.insertPropertyDetails(
        property = property,
        owner = owner,
        category = category,
        location = location,
        features = features
    )

    override suspend fun insertFeature(feature: Feature) = appDao.insertFeature(feature)

    override fun getAllProperties(): Flow<List<PropertyDetails>> = appDao.getAllProperties()

    override fun getSpecificProperty(propertyId: Int): Flow<PropertyDetails> = appDao.getProperty(propertyId)
    override suspend fun insertOwner(owner: Owner) = appDao.insertOwner(owner)

    override suspend fun insertCategory(category: Category) = appDao.insertCategory(category)

    override suspend fun insertProperty(property: Property) = appDao.insertProperty(property)
    override suspend fun insertLocation(location: Location) = appDao.insertLocation(location)

}