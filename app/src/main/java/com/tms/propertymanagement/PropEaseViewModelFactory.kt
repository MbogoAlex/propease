package com.propertymanagement.tms

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.propertymanagement.tms.ui.screens.SplashScreenViewModel
import com.propertymanagement.tms.ui.screens.WelcomeScreenViewModel
import com.propertymanagement.tms.ui.screens.accountManagement.LoginScreenViewModel
import com.propertymanagement.tms.ui.screens.accountManagement.ProfileScreenViewModel
import com.propertymanagement.tms.ui.screens.accountManagement.RegistrationScreenViewModel
import com.tms.propertymanagement.ui.screens.appContentPages.HomeScreenViewModel
import com.propertymanagement.tms.ui.screens.appContentPages.ListingDetailsScreenViewModel
import com.propertymanagement.tms.ui.screens.appContentPages.ListingsScreenViewModel
import com.propertymanagement.tms.ui.screens.propertyAdvertisementPages.PropertyUpdateScreenViewModel
import com.propertymanagement.tms.ui.screens.propertyAdvertisementPages.PropertyUploadScreenViewModel
import com.propertymanagement.tms.ui.screens.propertyAdvertisementPages.UserLivePropertiesScreenViewModel
import com.propertymanagement.tms.ui.screens.propertyAdvertisementPages.UserLivePropertyDetailsScreenViewModel
import com.tms.propertymanagement.connectivity.ConnectivityViewModel
import com.tms.propertymanagement.ui.screens.accountManagement.ProfileUpdateScreenViewModel
import com.tms.propertymanagement.ui.screens.accountManagement.profileVerification.ProfileVerificationScreenViewModel

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
            val savedStateHandle = this.createSavedStateHandle()
            HomeScreenViewModel(
                apiRepository = apiRepository,
                dsRepository = dsRepository,
                savedStateHandle = savedStateHandle
            )
        }

        // initialize ListingsScreen ViewModel
        initializer {

            val apiRepository = propEaseApplication().container.apiRepository
            val dsRepository = propEaseApplication().dsRepository
            val dbRepository = propEaseApplication().container.dbRepository
            ListingsScreenViewModel(
                apiRepository = apiRepository,
                dsRepository = dsRepository,
                dbRepository = dbRepository
            )
        }

        // initialize ListingDetailsScreen ViewModel
        initializer {
            val apiRepository = propEaseApplication().container.apiRepository
            val dbRepository = propEaseApplication().container.dbRepository
            val dsRepository = propEaseApplication().dsRepository
            val savedStateHandle = this.createSavedStateHandle()
            ListingDetailsScreenViewModel(
                apiRepository = apiRepository,
                dsRepository = dsRepository,
                savedStateHandle = savedStateHandle,
                dbRepository = dbRepository
            )
        }

        //initialize PropertyUploadScreen ViewModel
        initializer {
            val apiRepository = propEaseApplication().container.apiRepository
            val dsRepository = propEaseApplication().dsRepository
            PropertyUploadScreenViewModel(
                apiRepository = apiRepository,
                dsRepository = dsRepository
            )
        }

        // initialize UserLivePropertiesScreen ViewModel
        initializer {
            val apiRepository = propEaseApplication().container.apiRepository
            val dbRepository = propEaseApplication().container.dbRepository
            val dsRepository = propEaseApplication().dsRepository
            UserLivePropertiesScreenViewModel(
                apiRepository = apiRepository,
                dsRepository = dsRepository,
                dbRepository = dbRepository
            )
        }

        // initialize UserLivePropertyDetailsScreen ViewModel
        initializer {
            val apiRepository = propEaseApplication().container.apiRepository
            val dbRepository = propEaseApplication().container.dbRepository
            val dsRepository = propEaseApplication().dsRepository
            UserLivePropertyDetailsScreenViewModel(
                apiRepository = apiRepository,
                dsRepository = dsRepository,
                savedStateHandle = this.createSavedStateHandle(),
                dbRepository = dbRepository
            )
        }

        // initialize PropertyUpdateScreen ViewModel
        initializer {
            val apiRepository = propEaseApplication().container.apiRepository
            val dsRepository = propEaseApplication().dsRepository
            PropertyUpdateScreenViewModel(
                apiRepository = apiRepository,
                dsRepository = dsRepository,
                savedStateHandle = this.createSavedStateHandle()
            )
        }

        // initialize WelcomeScreen ViewModel
        initializer {
            val apiRepository = propEaseApplication().container.apiRepository
            val dsRepository = propEaseApplication().dsRepository
            val savedStateHandle = this.createSavedStateHandle()
            WelcomeScreenViewModel(
                apiRepository = apiRepository,
                dsRepository = dsRepository,
                savedStateHandle = savedStateHandle
            )
        }

        // initialize ProfileScreen ViewModel
        initializer {
            val apiRepository = propEaseApplication().container.apiRepository
            val dsRepository = propEaseApplication().dsRepository
            ProfileScreenViewModel(
                apiRepository = apiRepository,
                dsRepository = dsRepository
            )
        }

        // initialize ProfileUpdateScreen viewModel
        initializer {
            val apiRepository = propEaseApplication().container.apiRepository
            val dsRepository = propEaseApplication().dsRepository
            ProfileUpdateScreenViewModel(
                apiRepository = apiRepository,
                dsRepository = dsRepository
            )
        }

        // initialize ConnectivityViewModel
        initializer {
            val apiRepository = propEaseApplication().container.apiRepository
            ConnectivityViewModel(
                apiRepository = apiRepository
            )
        }

        // initialize ProfileVerificationScreenViewModel
        initializer {
            val apiRepository = propEaseApplication().container.apiRepository
            val dsRepository = propEaseApplication().dsRepository
            ProfileVerificationScreenViewModel(
                apiRepository = apiRepository,
                dsRepository = dsRepository
            )
        }

    }
}

fun CreationExtras.propEaseApplication(): PropEaseApp =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PropEaseApp)
