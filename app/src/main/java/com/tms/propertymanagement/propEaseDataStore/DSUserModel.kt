package com.propertymanagement.tms.propEaseDataStore

data class DSUserModel(
    val userId: Int?,
    val userName: String,
    val phoneNumber: String,
    val email: String,
    val password: String,
    val token: String
)
