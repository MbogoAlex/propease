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
        location: Location
    ) {
        insertProperty(property)
        insertOwner(owner)
        insertCategory(category)
        insertLocation(location)
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

    // query property details
    @Transaction
    @Query("SELECT * FROM property")
    fun getAllProperties(): Flow<List<PropertyDetails>>

    // query specific property
    @Transaction
    @Query("SELECT * FROM property WHERE propertyId = :propertyId")
    fun getProperty(propertyId: Int): Flow<PropertyDetails>


}