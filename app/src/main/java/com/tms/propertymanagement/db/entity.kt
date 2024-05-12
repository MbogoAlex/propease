package com.tms.propertymanagement.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "property", foreignKeys = [
    ForeignKey(entity = Category::class, parentColumns = ["categoryId"], childColumns = ["categoryId"], onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE),
    ForeignKey(entity = Owner::class, parentColumns = ["propertyId"], childColumns = ["propertyId"], onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE)
], indices = [Index(value = ["propertyId"], unique = true)]
)

data class Property(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val propertyId: Int = 0,
    val title: String = "",
    val description: String = "",
    val postedDate: String = "",
    val price: Double = 0.0,
    val propertyLocation: String = "",
    val rooms: Int = 0,
    val ownerId: Int = 0,
    val categoryId: Int = 0,
    val categoryName: String = "",
    val approved: Boolean = false,
    val paid: Boolean = false,
    val deletionTime: String = "",
    val fetchData: Boolean = true,
)
@Entity(tableName = "paymentDetails", foreignKeys = [
    ForeignKey(entity = Property::class, parentColumns = ["propertyId"], childColumns = ["propertyId"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)
])
data class PaymentDetails(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val paymentId: Int = 0,
    val propertyId: Int = 0,
    val partnerReferenceID: String? = null,
    val transactionID: String? = null,
    val msisdn: String? = null,
    val partnerTransactionID: String? = null,
    val payerTransactionID: String? = null,
    val receiptNumber: String? = null,
    val transactionAmount: Double? = null,
    val transactionStatus: String = "",
    val paymentComplete: Boolean = false,
    val createdAt: Long = 12345,
    val updatedAt: Long = 12345
)
@Entity(tableName = "owner", [Index(value = ["propertyId"], unique = true)])
data class Owner(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val ownerId: Int = 0,
    val propertyId: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = ""
)
@Entity(tableName = "category",
    indices = [Index(value = ["categoryId"], unique = true)])
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val categoryId: Int = 0,
    val name: String = ""
)
@Entity(tableName = "location", foreignKeys = [
    ForeignKey(entity = Property::class, parentColumns = ["propertyId"], childColumns = ["propertyId"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)
])
data class Location(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val propertyId: Int = 0,
    val county: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
@Entity(tableName = "feature", foreignKeys = [
    ForeignKey(entity = Property::class, parentColumns = ["propertyId"], childColumns = ["propertyId"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE),
], indices = [Index(value = ["propertyId"], unique = false)])
data class Feature(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val propertyId: Int = 0,
)

data class PropertyDetails(
    @Embedded val property: Property = Property(),

    @Relation(
        parentColumn = "ownerId",
        entityColumn = "ownerId"
    )
    val owner: Owner = Owner(),

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val category: Category = Category(),

    @Relation(
        parentColumn = "propertyId",
        entityColumn = "propertyId"
    )
    val location: Location? = Location(),

    @Relation(
        parentColumn = "propertyId",
        entityColumn = "propertyId"
    )
    val features: List<Feature> = emptyList(),

    @Relation(
        parentColumn = "propertyId",
        entityColumn = "propertyId"
    )
    val paymentDetails: PaymentDetails = PaymentDetails()
)