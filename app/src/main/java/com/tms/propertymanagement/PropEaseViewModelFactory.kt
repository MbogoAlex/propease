package com.tms.propertymanagement

import android.text.Spannable.Factory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tms.propertymanagement.ui.screens.accountManagement.LoginScreenViewModel
import com.tms.propertymanagement.ui.screens.accountManagement.RegistrationScreenViewModel

object PropEaseViewModelFactory {
    val Factory = viewModelFactory {
        // initialize LoginScreen ViewModel
        initializer {
            val apiRepository = propEaseApplication().container.apiRepository
            val dsRepository = propEaseApplication().dsRepository
            LoginScreenViewModel(
                apiRepository = apiRepository,
                dsRepository = dsRepository
            )
        }

        // initialize SplashScreen ViewModel
        initializer {
            val dsRepository = propEaseApplication().dsRepository
            SplashScreenViewModel(
                dsRepository = dsRepository
            )
        }

        // initialize RegistrationScreen ViewModel
        initializer {
            val apiRepository = propEaseApplication().container.apiRepository
            val dsRepository = propEaseApplication().dsRepository
            RegistrationScreenViewModel(
                apiRepository = apiRepository,
                dsRepository = dsRepository
            )
        }
    }
}

fun CreationExtras.propEaseApplication(): PropEaseApp =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PropEaseApp)