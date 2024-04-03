package com.tms.propertymanagement.utils

import java.text.NumberFormat
import java.util.Locale

object ReusableFunctions {
    fun checkIfEmailIsValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun formatMoneyValue(amount: Double): String {
        return  NumberFormat.getCurrencyInstance(Locale("en", "KE")).format(amount)
    }
}