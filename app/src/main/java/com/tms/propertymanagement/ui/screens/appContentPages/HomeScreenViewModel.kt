package com.tms.propertymanagement.ui.screens.appContentPages

import HomeScreenDestination
import android.util.Log
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanagement.tms.network.ApiRepository
import com.propertymanagement.tms.propEaseDataStore.DSRepository
import com.propertymanagement.tms.propEaseDataStore.DSUserModel
import com.propertymanagement.tms.utils.ReusableFunctions
import com.propertymanagement.tms.utils.ReusableFunctions.toLoggedInUserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MainNavigationPages {
    LISTINGS_SCREEN,
    MY_UNITS_SCREEN,
    ADVERTISE_SCREEN,
    NOTIFICATIONS_SCREEN,
    PROFILE_SCREEN,
    SIGN_UP_SCREEN,
    LOG_OUT_SCREEN
}
data class MainMenuItem (
    val label: String,
    val icon: Painter,
    val mainNavigationPage: MainNavigationPages
)

data class HomeScreenUiState(
    val userDetails: ReusableFunctions.LoggedInUserData = ReusableFunctions.LoggedInUserData(),
    val childScreen: String = "",
    val approvalStatus: String = "",
    val savedUserDetails: Boolean = false,
    val mainMenuItems: List<MainMenuItem> = mutableListOf<MainMenuItem>(),
    val screen: MainNavigationPages = MainNavigationPages.LISTINGS_SCREEN
)
class HomeScreenViewModel(
    private val apiRepository: ApiRepository,
    private val dsRepository: DSRepository,
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {
    private val _uiState = MutableStateFlow(value = HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    private val childScreen: String? = savedStateHandle[HomeScreenDestination.childScreen]


    fun loadStartUpDetails() {
        viewModelScope.launch {
            dsRepository.dsUserModel.collect() { dsUserModel ->
                _uiState.update {
                    it.copy(
                        userDetails = dsUserModel.toLoggedInUserData(),
                        childScreen = childScreen.takeIf { childScreen != null } ?: ""
                    )
                }
            }
        }
        if(!_uiState.value.savedUserDetails) {
            getUser()
        }

    }

    fun initializeMenuList(menuList: List<MainMenuItem>) {
        _uiState.update {
            it.copy(
                mainMenuItems = menuList
            )
        }
    }

    fun getUser() {
        viewModelScope.launch {
            try {
                val response = apiRepository.getUser(
                   userId =  _uiState.value.userDetails.userId!!,
                    token = _uiState.value.userDetails.token
                )
                if(response.isSuccessful) {

                    // save to datastore

                    val dsUserModel = DSUserModel(
                        userId = response.body()?.data?.profiles?.id,
                        userName = "${response.body()?.data?.profiles?.fname} ${response.body()?.data?.profiles?.lname!!}",
                        phoneNumber = response.body()?.data?.profiles?.phoneNumber!!,
                        email = response.body()?.data?.profiles?.email!!,
                        password = _uiState.value.userDetails.password,
                        token = _uiState.value.userDetails.token,
                        approvalStatus = response.body()?.data?.profiles?.approvalStatus!!
                    )
                    Log.i("SAVING_TO_DS", dsUserModel.toString())
                    dsRepository.saveUserData(dsUserModel)
                    _uiState.update {
                        it.copy(
                            savedUserDetails = true
                        )
                    }
                } else {
                    Log.e("ERROR_FETCHING_USER_PROPERTY_RESPONSE", response.toString())
                }
            } catch (e: Exception) {
                Log.e("ERROR_FETCHING_USER_PROPERTY_EXCEPTION", e.toString())
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            dsRepository.deletePreferences()
        }
    }

    fun resetChildScreen() {
        _uiState.update {
            it.copy(
                childScreen = ""
            )
        }
    }

    init {
        loadStartUpDetails()
    }
}