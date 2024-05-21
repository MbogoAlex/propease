import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.propertymanagement.tms.PropEaseViewModelFactory
import com.propertymanagement.tms.R
import com.propertymanagement.tms.nav.NavigationDestination
import com.propertymanagement.tms.ui.screens.accountManagement.LoginScreen
import com.propertymanagement.tms.ui.screens.appContentPages.ListingsScreen
import com.propertymanagement.tms.ui.screens.dummy.NotificationsScreen
import com.propertymanagement.tms.ui.screens.dummy.UnitsScreen
import com.propertymanagement.tms.ui.screens.propertyAdvertisementPages.UserLiveProperties
import com.tms.propertymanagement.ui.screens.accountManagement.ProfileScreen
import com.tms.propertymanagement.ui.screens.appContentPages.HomeScreenViewModel
import com.tms.propertymanagement.ui.screens.appContentPages.MainMenuItem
import com.tms.propertymanagement.ui.screens.appContentPages.MainNavigationPages
import kotlinx.coroutines.launch

object HomeScreenDestination: NavigationDestination {
    override val title: String = "Home Screen"
    override val route: String = "home-screen"
    val childScreen: String = "childScreen"
    val routeWithArgs: String = "$route/{$childScreen}"

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navigateToSpecificProperty: (propertyId: String) -> Unit,
    navigateToSpecificUserProperty: (propertyId: String) -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToHomeScreenWithArguments: (childScreen: String) -> Unit,
    navigateToLoginScreenWithoutArgs: () -> Unit,
    navigateToLoginScreenWithArgs: (phoneNumber: String, password: String) -> Unit,
    navigateToRegistrationScreen: () -> Unit,
    navigateToUpdateProfileScreen: () -> Unit,
    navigateToProfileVerificationScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: HomeScreenViewModel = viewModel(factory = PropEaseViewModelFactory.Factory)
    val uiState by viewModel.uiState.collectAsState()
    var currentScreen by rememberSaveable {
        mutableStateOf(MainNavigationPages.LISTINGS_SCREEN)
    }
    var screenLabel by rememberSaveable {
        mutableStateOf("Find Properties")
    }



    var loggedInMenuItems = listOf<MainMenuItem>(
        MainMenuItem(
            label = "Find Properties",
            icon = painterResource(id = R.drawable.house),
            mainNavigationPage = MainNavigationPages.LISTINGS_SCREEN
        ),
        MainMenuItem(
            label = "Bought / Sold Units",
            icon = painterResource(id = R.drawable.house),
            mainNavigationPage = MainNavigationPages.MY_UNITS_SCREEN
        ),
        MainMenuItem(
            label = "Advertise",
            icon = painterResource(id = R.drawable.advertise),
            mainNavigationPage = MainNavigationPages.ADVERTISE_SCREEN
        ),
        MainMenuItem(
            label = "Notifications",
            icon = painterResource(id = R.drawable.notifications),
            mainNavigationPage = MainNavigationPages.NOTIFICATIONS_SCREEN
        ),
        MainMenuItem(
            label = "Profile",
            icon = painterResource(id = R.drawable.person),
            mainNavigationPage = MainNavigationPages.PROFILE_SCREEN
        ),
        MainMenuItem(
            label = "Log out",
            icon = painterResource(id = R.drawable.logout),
            mainNavigationPage = MainNavigationPages.LOG_OUT_SCREEN
        ),
    )

    var loggedOutMenuItems = listOf<MainMenuItem>(
        MainMenuItem(
            label = "Find Properties",
            icon = painterResource(id = R.drawable.house),
            mainNavigationPage = MainNavigationPages.LISTINGS_SCREEN
        ),
        MainMenuItem(
            label = "Bought / Sold Units",
            icon = painterResource(id = R.drawable.house),
            mainNavigationPage = MainNavigationPages.MY_UNITS_SCREEN
        ),
        MainMenuItem(
            label = "Advertise",
            icon = painterResource(id = R.drawable.advertise),
            mainNavigationPage = MainNavigationPages.ADVERTISE_SCREEN
        ),
        MainMenuItem(
            label = "Notifications",
            icon = painterResource(id = R.drawable.notifications),
            mainNavigationPage = MainNavigationPages.NOTIFICATIONS_SCREEN
        ),
        MainMenuItem(
            label = "Sign in",
            icon = painterResource(id = R.drawable.login),
            mainNavigationPage = MainNavigationPages.SIGN_UP_SCREEN
        ),
    )

    if(uiState.userDetails.userId == 0 || uiState.userDetails.userId == null) {
        viewModel.initializeMenuList(loggedOutMenuItems)
    } else {
        viewModel.initializeMenuList(loggedInMenuItems)
    }

    var showLogoutDialog by rememberSaveable {
        mutableStateOf(false)
    }



    if(uiState.childScreen == "advertisement-screen") {
        Log.i("NAVIGATING_TO_ADVRETS", "advertisement-screen")
        currentScreen = MainNavigationPages.ADVERTISE_SCREEN
        viewModel.resetChildScreen()
    } else if(uiState.childScreen == "profile-screen") {
        currentScreen = MainNavigationPages.PROFILE_SCREEN
        viewModel.resetChildScreen()
    } else if(uiState.childScreen == "logged-out") {
        viewModel.initializeMenuList(loggedOutMenuItems)
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    if(showLogoutDialog) {
        AlertDialog(
            title = {
                Text(text = "Logout confirmation")
            },
            text = {
                Text(text = "Are you sure you want to log out?")
            },
            onDismissRequest = {
                showLogoutDialog = !showLogoutDialog
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.logout()
                    showLogoutDialog = !showLogoutDialog
                    Toast.makeText(context, "You are logged out", Toast.LENGTH_SHORT).show()
                    navigateToHomeScreenWithArguments("logged-out")
                }) {
                    Text(text = "Log out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = !showLogoutDialog }) {
                    Text(text = "Dismiss")
                }
            }
        )
    }



    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 8.dp,
                            top = 16.dp
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_placeholder),
                        contentDescription = "Profile picture",
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(90.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = uiState.userDetails.userName,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Divider()
                for(menuItem in uiState.mainMenuItems) {
                    NavigationDrawerItem(
                        modifier = Modifier
                            .padding(8.dp),
                        label = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = menuItem.icon,
                                        contentDescription = menuItem.label
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = menuItem.label)
                                }
                        },
                        selected = menuItem.mainNavigationPage == currentScreen,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                currentScreen = menuItem.mainNavigationPage
                            }

                        }
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    scope.launch {
                        if(drawerState.isClosed) drawerState.open() else drawerState.close()
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.drawable_menu),
                        contentDescription = "Navigation menu"
                    )

                }
                if(uiState.userDetails.userName.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hey, ${uiState.userDetails.userName}",
                            fontWeight = FontWeight.Bold
                        )
                        if(uiState.userDetails.approvalStatus.lowercase() == "approved") {
                            Icon(
                                painter = painterResource(id = R.drawable.verified),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(15.dp)
                            )
                        }
                    }
                } else {
                    TextButton(onClick = { navigateToLoginScreenWithoutArgs() }) {
                        Text(text = "Login")
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = screenLabel,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            when(currentScreen) {
                MainNavigationPages.LISTINGS_SCREEN -> {
                    screenLabel = "Properties"
                    ListingsScreen(
                        token = uiState.userDetails.token,
                        navigateToSpecificProperty = navigateToSpecificProperty
                    )
                }
                MainNavigationPages.MY_UNITS_SCREEN -> {
                    screenLabel = "Properties"
                    UnitsScreen(
                        navigateToHomeScreen = navigateToHomeScreen
                    )
                }
//                MainNavigationPages.ADVERTISE_SCREEN -> PropertyUploadScreen(
//                    navigateToListingsScreen = {
//                        navigateToHomeScreen()
//                    }
//                )
                MainNavigationPages.ADVERTISE_SCREEN -> {
                    screenLabel = "Properties"
                    UserLiveProperties(
                        navigateToSpecificUserProperty = {
                            navigateToSpecificUserProperty(it)
                        },
                        navigateToHomeScreen = navigateToHomeScreen,
                        navigateToHomeScreenWithArguments = navigateToHomeScreenWithArguments,
                        navigateToLoginScreenWithoutArgs = navigateToLoginScreenWithoutArgs,
                        navigateToLoginScreenWithArgs = navigateToLoginScreenWithArgs,
                        navigateToProfileVerificationScreen = navigateToProfileVerificationScreen
//                    navigateToUpdateProperty = { /*TODO*/ }
                    )
                }
                MainNavigationPages.NOTIFICATIONS_SCREEN -> {
                    screenLabel = "Notifications"
                    NotificationsScreen(
                        navigateToHomeScreen = navigateToHomeScreen
                    )
                }
                MainNavigationPages.PROFILE_SCREEN -> {
                    screenLabel = "Profile"
                    ProfileScreen(
                        navigateToHomeScreenWithArgs = navigateToHomeScreenWithArguments,
                        navigateToLoginScreenWithoutArgs = navigateToLoginScreenWithoutArgs,
                        navigateToHomeScreen = navigateToHomeScreen,
                        navigateToUpdateProfileScreen = navigateToUpdateProfileScreen,
                        navigateProfileVerificationScreen = navigateToProfileVerificationScreen
                    )
                }
                MainNavigationPages.SIGN_UP_SCREEN -> {
                    LoginScreen(
                        navigateToPreviousScreen = navigateToHomeScreen,
                        navigateToRegistrationScreen = navigateToRegistrationScreen,
                        navigateToHomeScreen = navigateToHomeScreen
                    )
                }
                MainNavigationPages.LOG_OUT_SCREEN -> {
                    showLogoutDialog = true
                    currentScreen = MainNavigationPages.LISTINGS_SCREEN
                    viewModel.initializeMenuList(loggedOutMenuItems)
                }
            }
        }
    }



}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        navigateToSpecificProperty = {},
        navigateToHomeScreen = {},
        navigateToSpecificUserProperty = {},
        navigateToHomeScreenWithArguments = {},
        navigateToLoginScreenWithoutArgs = {},
        navigateToRegistrationScreen = {},
        navigateToUpdateProfileScreen = {},
        navigateToLoginScreenWithArgs = {phoneNumber, password ->  },
        navigateToProfileVerificationScreen = {}
    )
}

