package com.propertymanagement.tms.utils

import com.propertymanagement.tms.propEaseDataStore.DSUserModel
import java.text.NumberFormat
import java.util.Locale

object ReusableFunctions {

    enum class ExecutionStatus {
        INITIAL,
        LOADING,
        SUCCESS,
        FAIL
    }

    data class LoggedInUserData(
        val userId: Int? = 0,
        val userName: String = "",
        val phoneNumber: String = "",
        val email: String = "",
        val password: String = "",
        val token: String = "",
        val approvalStatus: String = ""
    )
    fun checkIfEmailIsValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun formatMoneyValue(amount: Double): String {
        return  NumberFormat.getCurrencyInstance(Locale("en", "KE")).format(amount)
    }

    fun DSUserModel.toLoggedInUserData() : LoggedInUserData = LoggedInUserData(
        userId = userId,
        userName = userName,
        phoneNumber = phoneNumber,
        email = email,
        password = password,
        token = token,
        approvalStatus = approvalStatus
    )
}