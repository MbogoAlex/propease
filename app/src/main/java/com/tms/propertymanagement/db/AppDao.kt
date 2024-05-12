package com.tms.propertymanagement.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
@Dao
interface AppDao {
    // insert property details
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPropertyDetails(
        property: Property,
        owner: Owner,
        category: Category,
        location: Location,
        features: List<Feature>
    ) {
        insertProperty(property)
        insertOwner(owner)
        insertCategory(category)
        insertLocation(location)
        for(feature in features) {
            insertFeature(feature)
        }
    }

    // insert category
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: Category)

    // insert property
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProperty(property: Property)

    // insert owner
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOwner(owner: Owner)

    // insert location
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLocation(location: Location)

    // insert feature
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFeature(feature: Feature)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPaymentDetails(paymentDetails: PaymentDetails)


    // query property details
    @Transaction
    @Query("SELECT * FROM property")
    fun getAllProperties(): Flow<List<PropertyDetails>>

    // query specific property
    @Transaction
    @Query("SELECT * FROM property WHERE propertyId = :propertyId")
    fun getProperty(propertyId: Int): Flow<PropertyDetails>

    @Transaction
    @Query("SELECT * FROM property WHERE (property.fetchData = :fetchData) AND (:location IS NULL OR property.propertyLocation LIKE :location) AND (:category IS NULL OR property.categoryName = :category) AND (:rooms IS NULL OR property.rooms = :rooms) GROUP BY property.propertyId")
    fun filterProperties(fetchData: Boolean, location: String?, rooms: Int?, category: String?): Flow<List<PropertyDetails>>
    @Transaction
    @Query("SELECT * FROM property WHERE property.ownerId = :ownerId GROUP BY property.propertyId")
    fun getUserProperties(ownerId: Int): Flow<List<PropertyDetails>>
}