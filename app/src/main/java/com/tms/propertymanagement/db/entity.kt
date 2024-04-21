package com.tms.propertymanagement.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.net.Inet4Address

@Entity(tableName = "property", foreignKeys = [
    ForeignKey(entity = Category::class, parentColumns = ["categoryId"], childColumns = ["categoryId"], onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE)
])
data class Property(
    @PrimaryKey(autoGenerate = true)
    val propertyId: Int = 0,
    val title: String,
    val description: String,
    val price: Double,
    val ownerId: Int,
    val categoryId: Int,
    val locationId: Int
)
@Entity(tableName = "owner", foreignKeys = [
    ForeignKey(Property::class, parentColumns = ["ownerId"], childColumns = ["ownerId"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)
])
data class Owner(
    @PrimaryKey(autoGenerate = true)
    val ownerId: Int = 0,
    val name: String,
    val phoneNumber: String
)
@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int = 0,
    val name: String
)
@Entity(tableName = "location", foreignKeys = [
    ForeignKey(entity = Property::class, parentColumns = ["locationId"], childColumns = ["locationId"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)
])
data class Location(
    @PrimaryKey(autoGenerate = true)
    val locationId: Int = 0,
    val county: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)
@Entity(tableName = "feature", foreignKeys = [
    ForeignKey(entity = Property::class, parentColumns = ["propertyId"], childColumns = ["propertyId"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)
])
data class Feature(
    @PrimaryKey(autoGenerate = true)
    val featureId: Int = 0,
    val name: String,
    val propertyId: Int,
)

data class PropertyDetails(
    @Embedded val property: Property,

    @Relation(
        parentColumn = "ownerId",
        entityColumn = "ownerId"
    )
    val owner: Owner,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val category: Category,

    @Relation(
        parentColumn = "locationId",
        entityColumn = "locationId"
    )
    val location: Location,

    @Relation(
        parentColumn = "propertyId",
        entityColumn = "propertyId"
    )
    val features: List<Feature>
)