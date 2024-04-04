package com.tms.propertymanagement

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tms.propertymanagement.ui.screens.SplashScreenViewModel
import com.tms.propertymanagement.ui.screens.accountManagement.LoginScreenViewModel
import com.tms.propertymanagement.ui.screens.accountManagement.RegistrationScreenViewModel
import com.tms.propertymanagement.ui.screens.appContentPages.HomeScreenViewModel
import com.tms.propertymanagement.ui.screens.appContentPages.ListingsScreenViewModel

object PropEaseViewModelFactory {
    val Factory = viewModelFactory {
        // initialize LoginScreen ViewModel
        initializer {
            val apiRepository = propEaseApplication().container.apiRepository
            val dsRepository = propEaseApplication().dsRepository
            LoginScreenViewModel(
                apiRepository = apiRepository,
                dsRepository = dsRepository,
                savedStateHandle = this.createSavedStateHandle()
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

        // initialize HomeScreen ViewModel
        initializer {
            val apiRepository = propEaseApplication().container.apiRepository
            val dsRepository = propEaseApplication().dsRepository
            HomeScreenViewModel(
                apiRepository = apiRepository,
                dsRepository = dsRepository
            )
        }

        // initialize ListingsScreen ViewModel
        initializer {
            val apiRepository = propEaseApplication().container.apiRepository
            val dsRepository = propEaseApplication().dsRepository
            ListingsScreenViewModel(
                apiRepository = apiRepository,
                dsRepository = dsRepository
            )
        }
    }
}

fun CreationExtras.propEaseApplication(): PropEaseApp =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PropEaseApp)