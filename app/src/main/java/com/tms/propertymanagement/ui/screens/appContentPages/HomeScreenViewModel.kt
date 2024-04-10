package com.tms.propertymanagement.ui.screens.appContentPages

import HomeScreenDestination
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanagement.tms.network.ApiRepository
import com.propertymanagement.tms.propEaseDataStore.DSRepository
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
    }

    fun initializeMenuList(menuList: List<MainMenuItem>) {
        _uiState.update {
            it.copy(
                mainMenuItems = menuList
            )
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