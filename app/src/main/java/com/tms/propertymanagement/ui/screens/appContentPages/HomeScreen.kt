import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tms.propertymanagement.PropEaseViewModelFactory
import com.tms.propertymanagement.R
import com.tms.propertymanagement.nav.NavigationDestination
import com.tms.propertymanagement.ui.screens.appContentPages.HomeScreenViewModel
import com.tms.propertymanagement.ui.screens.appContentPages.ListingsScreen
import com.tms.propertymanagement.ui.screens.propertyAdvertisementPages.PropertyUploadScreen
import com.tms.propertymanagement.ui.screens.propertyAdvertisementPages.UserLiveProperties
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

object HomeScreenDestination: NavigationDestination {
    override val title: String = "Home Screen"
    override val route: String = "home-screen"
    val childScreen: String = "childScreen"
    val routeWithArgs: String = "$route/{$childScreen}"

}
enum class MainNavigationPages {
    LISTINGS_SCREEN,
    MY_UNITS_SCREEN,
    ADVERTISE_SCREEN,
    NOTIFICATIONS_SCREEN,
    PROFILE_SCREEN
}
data class MainMenuItem (
    val label: String,
    val icon: Painter,
    val mainNavigationPage: MainNavigationPages
)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navigateToSpecificProperty: (propertyId: String) -> Unit,
    navigateToSpecificUserProperty: (propertyId: String) -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToHomeScreenWithArguments: (childScreen: String) -> Unit,
    navigateToLoginScreenWithoutArgs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: HomeScreenViewModel = viewModel(factory = PropEaseViewModelFactory.Factory)
    val uiState by viewModel.uiState.collectAsState()
    var currentScreen by rememberSaveable {
        mutableStateOf(MainNavigationPages.LISTINGS_SCREEN)
    }
    var screenLabel by rememberSaveable {
        mutableStateOf("Listings")
    }
    var mainMenuItems = listOf<MainMenuItem>(
        MainMenuItem(
            label = "Listings",
            icon = painterResource(id = R.drawable.house),
            mainNavigationPage = MainNavigationPages.LISTINGS_SCREEN
        ),
        MainMenuItem(
            label = "My Units",
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
    )

    if(uiState.childScreen == "advertisement-screen") {
        currentScreen = MainNavigationPages.ADVERTISE_SCREEN
        viewModel.resetChildScreen()
    } 

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()



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
                for(menuItem in mainMenuItems) {
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
                .padding(16.dp)
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
                    Text(
                        text = "Hey, ${uiState.userDetails.userName}",
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    TextButton(onClick = { navigateToLoginScreenWithoutArgs() }) {
                        Text(text = "Login")
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = screenLabel,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }
            when(currentScreen) {
                MainNavigationPages.LISTINGS_SCREEN -> {
                    screenLabel = "Listings"
                    ListingsScreen(
                        token = uiState.userDetails.token,
                        navigateToSpecificProperty = navigateToSpecificProperty
                    )
                }
                MainNavigationPages.MY_UNITS_SCREEN -> {}
//                MainNavigationPages.ADVERTISE_SCREEN -> PropertyUploadScreen(
//                    navigateToListingsScreen = {
//                        navigateToHomeScreen()
//                    }
//                )
                MainNavigationPages.ADVERTISE_SCREEN -> {
                    screenLabel = "Live"
                    UserLiveProperties(
                        navigateToSpecificUserProperty = {
                            navigateToSpecificUserProperty(it)
                        },
                        navigateToHomeScreen = navigateToHomeScreen,
                        navigateToHomeScreenWithArguments = navigateToHomeScreenWithArguments,
                        navigateToLoginScreenWithoutArgs = navigateToLoginScreenWithoutArgs
//                    navigateToUpdateProperty = { /*TODO*/ }
                    )
                }
                MainNavigationPages.NOTIFICATIONS_SCREEN -> {}
                MainNavigationPages.PROFILE_SCREEN -> {}
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
        navigateToLoginScreenWithoutArgs = {}
    )
}

