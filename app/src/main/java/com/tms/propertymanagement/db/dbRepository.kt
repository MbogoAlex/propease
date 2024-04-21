package com.tms.propertymanagement.db

import kotlinx.coroutines.flow.Flow

interface DBRepository {
    // insert property details
    suspend fun insertPropertyDetails(
        property: Property,
        owner: Owner,
        category: Category,
        location: Location
    )

    // get all properties
    fun getAllProperties(): Flow<List<PropertyDetails>>

    // get specific property
    fun getSpecificProperty(propertyId: Int): Flow<PropertyDetails>
}

class OfflineRepository(private val appDao: AppDao): DBRepository {
    override suspend fun insertPropertyDetails(
        property: Property,
        owner: Owner,
        category: Category,
        location: Location
    ) = appDao.insertPropertyDetails(
        property = property,
        owner = owner,
        category = category,
        location = location
    )

    override fun getAllProperties(): Flow<List<PropertyDetails>> = appDao.getAllProperties()

    override fun getSpecificProperty(propertyId: Int): Flow<PropertyDetails> = appDao.getProperty(propertyId)

}