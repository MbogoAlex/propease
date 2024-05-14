package com.propertymanagement.tms.ui.screens.appContentPages

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.propertymanagement.tms.PropEaseViewModelFactory
import com.propertymanagement.tms.R
import com.propertymanagement.tms.apiModel.PropertyData
import com.propertymanagement.tms.ui.theme.PropEaseTheme
import com.tms.propertymanagement.connectivity.ConnectivityViewModel
import kotlinx.coroutines.delay

@Composable
fun ListingsScreen(
    token: String,
    navigateToSpecificProperty: (propertyId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = (LocalContext.current as? Activity)

    BackHandler(onBack = {activity?.finish()})

    val context = LocalContext.current

    val connectivityViewModel: ConnectivityViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = PropEaseViewModelFactory.Factory)

    LaunchedEffect(Unit) {
        connectivityViewModel.checkConnectivity(context)
    }

    val viewModel: ListingsScreenViewModel = viewModel(factory = PropEaseViewModelFactory.Factory)
    val uiState by viewModel.uiState.collectAsState()

    val isConnected by connectivityViewModel.isConnected.observeAsState(false)

    var fetchedFromDB by remember {
        mutableStateOf(false)
    }

    var active by remember {
        mutableStateOf(false)
    }

    var inactive by remember {
        mutableStateOf(false)
    }

    if(isConnected && !active) {
        Log.i("SETTING_CONNECTIVITY", isConnected.toString())
        viewModel.setConnectionStatus(true)

        active = true
    } else if(!isConnected && active || !isConnected && !inactive) {
        Log.i("FETCH_FROM_LITE", true.toString())
        viewModel.setConnectionStatus(false)
        active = false
        inactive = true
    }



    Scaffold(
        floatingActionButton = {
            if(!uiState.internetPresent) {
                FloatingActionButton(onClick = { viewModel.fetchCategories() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh page"
                    )
                }
            }
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
        ) {
            ListingsFilterSection(
                uiState = uiState,
                viewModel = viewModel,
                isConnected = uiState.isConnected,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            ListingItems(
                navigateToSpecificProperty = navigateToSpecificProperty,
                uiState = uiState,
                viewModel = viewModel,
                modifier = Modifier.weight(1f)
            )

        }
    }

}

@Composable
fun ListingsFilterSection(
    uiState: ListingsScreenUiState,
    viewModel: ListingsScreenViewModel,
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {

    Column {
        LocationSearchForm(
            leadingIcon = painterResource(id = R.drawable.locations),
            labelText = "Location",
            value = uiState.location,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text,
            ),
            onValueChanged = {
                if(isConnected) {
                    viewModel.fetchFilteredProperties(
                        location = it,
                        rooms = null,
                        categoryId = null,
                        categoryName = null
                    )
                } else {
                    viewModel.fetchFilteredDBProperties(
                        location = it,
                        rooms = null,
                        categoryId = null,
                        categoryName = null
                    )
                }

                viewModel.turnOnFiltering()
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
        ) {
            CategorySelection(
                uiState = uiState,
                onChangeCategory = {location, rooms, categoryId, categoryName ->
                    if(isConnected) {
                        viewModel.fetchFilteredProperties(
                            location = location,
                            rooms = rooms,
                            categoryId = categoryId,
                            categoryName = categoryName
                        )
                    } else {
                        viewModel.fetchFilteredDBProperties(
                            location = location,
                            rooms = rooms,
                            categoryId = categoryId,
                            categoryName = categoryName
                        )
                    }
                    viewModel.clearNumberOfRoomsSelected()
                    viewModel.turnOnFiltering()
                }
            )
            Spacer(modifier = Modifier.width(10.dp))
            NumberOfRoomsSelection(
                uiState = uiState,
                onChangeNumberOfRooms = {location, rooms, categoryId, categoryName ->
                    if(isConnected) {
                        viewModel.fetchFilteredProperties(
                            location = location,
                            rooms = rooms,
                            categoryId = categoryId,
                            categoryName = categoryName
                        )
                    } else {
                        viewModel.fetchFilteredDBProperties(
                            location = location,
                            rooms = rooms,
                            categoryId = categoryId,
                            categoryName = categoryName
                        )
                    }

                    viewModel.turnOnFiltering()
                },
            )
            Spacer(modifier = Modifier.weight(1f))
            if(uiState.filteringOn) {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .clickable {
                            viewModel.unfilter()
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                }
            }

        }
        Spacer(modifier = Modifier.height(5.dp))
        if(!isConnected) {
            Text(
                text = "Check your internet connection",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
    }
}

@Composable
fun LocationSearchForm(
    leadingIcon: Painter,
    labelText: String,
    value: String,
    keyboardOptions: KeyboardOptions,
    onValueChanged: (newValue: String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        shape = RoundedCornerShape(10.dp),
        leadingIcon = {
            Icon(
                painter = leadingIcon,
                contentDescription = null
            )
        },

        label = {
            Text(text = labelText)
        },
        value = value,
        keyboardOptions = keyboardOptions,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        onValueChange = onValueChanged,
        modifier = modifier
    )

}

@Composable
fun NumberOfRoomsSelection(
    uiState: ListingsScreenUiState,
    onChangeNumberOfRooms: (location: String?, rooms: String?, categoryId: Int?, categoryName: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember {
        mutableStateOf(false)
    }


    val defaultRooms = listOf(
        "Bedsitter - Rental, Airbnb, On sale",
        "One bedroom - Rental, Airbnb, On sale",
        "Two bedrooms - Rental, Airbnb, On sale",
        "Three bedrooms - Rental, Airbnb, On sale",
        "Four bedrooms - Rental, Airbnb, On sale",
        "Five bedrooms - Rental, Airbnb, On sale",
        "Single room - Shop, Office, On sale",
        "Two rooms - Shop, Office, On sale",
        "Three rooms - Shop, Office, On sale",

        )

    var dropDownIcon: ImageVector
    if(expanded) {
        dropDownIcon = Icons.Default.KeyboardArrowUp
    } else {
        dropDownIcon = Icons.Default.KeyboardArrowDown
    }

    Column {
        Button(
            shape = RoundedCornerShape(10.dp),
            onClick = {
                expanded = !expanded
                Log.i("No_ROOMS", uiState.rooms.toString())
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
            ) {
                Text(
                    text = "No. Rooms".takeIf { uiState.numberOfRoomsSelected.isEmpty() }
                        ?: uiState.numberOfRoomsSelected,
                    modifier = Modifier

//                        .widthIn(120.dp)
                )
                Icon(
                    imageVector = dropDownIcon,
                    contentDescription = null
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (uiState.rooms.isEmpty()) {
                defaultRooms.forEachIndexed { index, i ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = i
                            )
                        },
                        onClick = {
                            onChangeNumberOfRooms(
                                null, i, null,null
                            )

                            expanded = !expanded
                        }
                    )
                }

            } else {
                uiState.rooms.forEachIndexed { index, i ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = i
                            )
                        },
                        onClick = {
                            onChangeNumberOfRooms(
                                null, i, null,null
                            )

                            expanded = !expanded
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CategorySelection(
    uiState: ListingsScreenUiState,
    onChangeCategory: (location: String?, rooms: String?, categoryId: Int?, categoryName: String?) -> Unit,
    modifier: Modifier = Modifier
) {

    var expanded by remember {
        mutableStateOf(false)
    }
    var dropDownIcon: ImageVector
    if(expanded) {
        dropDownIcon = Icons.Default.KeyboardArrowUp
    } else {
        dropDownIcon = Icons.Default.KeyboardArrowDown
    }
    Column {
        Button(
            shape = RoundedCornerShape(10.dp),
            onClick = { expanded = !expanded }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier

            ) {
                Text(
                    text = "Category".takeIf { uiState.categoryNameSelected.isEmpty() } ?: uiState.categoryNameSelected,
                    modifier = Modifier
//                        .padding(10.dp)
//                        .widthIn(120.dp)
                )
                Icon(
                    imageVector = dropDownIcon,
                    contentDescription = null
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            uiState.categories.forEachIndexed { index, i ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = i.name
                        )
                    },
                    onClick = {
                        onChangeCategory(
                            null, null, i.id, i.name
                        )
                        expanded = !expanded
                    }
                )
            }
        }
    }
}

@Composable
fun ListingItems(
    navigateToSpecificProperty: (propertyId: String) -> Unit,
    uiState: ListingsScreenUiState,
    viewModel: ListingsScreenViewModel,
    modifier: Modifier = Modifier
) {
    var showText by remember { mutableStateOf(false) }

    var properties = uiState.properties.reversed()
    if(uiState.fetchingStatus == FetchingStatus.LOADING) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    } else if (uiState.fetchingStatus == FetchingStatus.SUCCESS) {
        if(!uiState.internetPresent) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Internet not present")
            }
        }
        if (properties.isEmpty()) {
            LaunchedEffect(Unit) {
                delay(2000L)
                showText = true
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if(showText) {
                    Text(text = "Properties not added yet")
                } else {
                    CircularProgressIndicator()
                }
            }
        } else {

            LazyVerticalGrid(
                columns = GridCells.Fixed(2)
            ) {
                items(properties) {
                    ListingItem(
                        navigateToSpecificProperty = navigateToSpecificProperty,
                        property = it,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                }
            }
        }
    }

}

@Composable
fun ListingItem(
    property: PropertyData,
    navigateToSpecificProperty: (propertyId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable {
                navigateToSpecificProperty(property.propertyId.toString())
            }
    ) {
        Column {
            if(property.images.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(property.images.first().name)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(id = R.drawable.loading_img),
                    error = painterResource(id = R.drawable.ic_broken_image),
                    contentScale = ContentScale.Crop,
                    contentDescription = property.title,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .height(140.dp)
                        .fillMaxWidth()
                )
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .alpha(0.5f)
                        .height(140.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .padding(5.dp)
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(5.dp)
                        )
                ) {
                    Text(text = "No image")
                }

            }

            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(
                    text = property.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = property.location.county
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "${property.location.county}, ${property.location.address}",
                        fontSize = 13.sp,
                        maxLines = 1,
                        fontWeight = FontWeight.Light,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black
                    )
                ) {
                    Text(
                        text = property.category.takeIf { it.length <= 6 } ?: "${property.category.substring(0, 4)}...",
                        fontSize = 11.sp,
                        color = Color.White,
                        modifier = Modifier
                            .padding(
                                start = 10.dp,
                                top = 5.dp,
                                end = 10.dp,
                                bottom = 5.dp
                            )
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListingItemPreview(
    modifier: Modifier = Modifier
) {
    val uiState = ListingsScreenUiState()
    PropEaseTheme {
        ListingItem(
            uiState.properties[0],
            navigateToSpecificProperty = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NumberOfRoomsSelectionPreview(
    modifier: Modifier = Modifier
) {
    val viewModel: ListingsScreenViewModel = viewModel()
    PropEaseTheme {
        NumberOfRoomsSelection(
            uiState = ListingsScreenUiState(),
            onChangeNumberOfRooms = {location, rooms, categoryId, categoryName ->  }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ListingsFilterSectionPreview(
    modifier: Modifier = Modifier
) {
    val viewModel: ListingsScreenViewModel = viewModel()
    PropEaseTheme {
        ListingsFilterSection(
            uiState = ListingsScreenUiState(),
            viewModel = viewModel,
            isConnected = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ListingsScreenPreview(
    modifier: Modifier = Modifier
) {
    PropEaseTheme {
        ListingsScreen(
            token = "",
            navigateToSpecificProperty = {},
        )
    }
}