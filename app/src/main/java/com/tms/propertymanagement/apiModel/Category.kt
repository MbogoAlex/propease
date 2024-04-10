package com.propertymanagement.tms.apiModel

import kotlinx.serialization.Serializable

// fetch properties categories
@Serializable
data class CategoryResponseBody(
    val statusCode: Int,
    val message: String,
    val data: CategoryData
)
@Serializable

data class CategoryData(
    val categories: List<Category>
)
@Serializable

data class Category(
    val id: Int,
    val name: String
)