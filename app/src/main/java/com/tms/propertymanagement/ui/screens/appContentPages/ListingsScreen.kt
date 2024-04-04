package com.tms.propertymanagement.ui.screens.appContentPages

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.tms.propertymanagement.PropEaseViewModelFactory
import com.tms.propertymanagement.R
import com.tms.propertymanagement.apiModel.PropertyData
import com.tms.propertymanagement.ui.theme.PropEaseTheme

@Composable
fun ListingsScreen(
    token: String,
    navigateToSpecificProperty: (propertyId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ListingsScreenViewModel = viewModel(factory = PropEaseViewModelFactory.Factory)
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        ListingsFilterSection(
            uiState = uiState,
            viewModel = viewModel
        )
        Spacer(modifier = Modifier.height(16.dp))
        ListingItems(
            navigateToSpecificProperty = navigateToSpecificProperty,
            uiState = uiState,
            viewModel = viewModel
        )
    }
}

@Composable
fun ListingsFilterSection(
    uiState: ListingsScreenUiState,
    viewModel: ListingsScreenViewModel,
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
                             viewModel.fetchFilteredProperties(
                                 location = it,
                                 rooms = null,
                                 categoryId = null,
                                 categoryName = null
                             )
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            NumberOfRoomsSelection(
                uiState = uiState,
                viewModel = viewModel
            )
            Spacer(modifier = Modifier.width(20.dp))
            CategorySelection(
                uiState = uiState,
                viewModel = viewModel
            )
        }

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
    viewModel: ListingsScreenViewModel,
    modifier: Modifier = Modifier
) {
    val rooms = listOf<Int>(1, 2, 3, 4, 5, 6, 7, 8)
//    var selectedRoom by remember {
//        mutableIntStateOf(0)
//    }
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
        Card(
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        expanded = !expanded
                    }
            ) {
                Text(
                    text = "No. Rooms".takeIf { uiState.numberOfRoomsSelected.isEmpty() }
                        ?: "${uiState.numberOfRoomsSelected} room".takeIf { uiState.numberOfRoomsSelected.toInt() == 1 }
                        ?: "${uiState.numberOfRoomsSelected} rooms",
                    modifier = Modifier
                        .padding(10.dp)
                        .widthIn(120.dp)
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
            rooms.forEachIndexed { index, i ->
                DropdownMenuItem(
                    text = {
                           Text(
                               text = "1 room".takeIf { i == 1 } ?: "$i rooms"
                           )
                    },
                    onClick = {
                        viewModel.fetchFilteredProperties(
                            location = null,
                            rooms = i,
                            categoryId = null,
                            categoryName = null
                        )
                        expanded = !expanded
                    }
                )
            }
        }
    }
}

@Composable
fun CategorySelection(
    uiState: ListingsScreenUiState,
    viewModel: ListingsScreenViewModel,
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
        Card(
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        expanded = !expanded
                    }
            ) {
                Text(
                    text = "Category".takeIf { uiState.categoryNameSelected.isEmpty() } ?: uiState.categoryNameSelected,
                    modifier = Modifier
                        .padding(10.dp)
                        .widthIn(120.dp)
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
                        viewModel.fetchFilteredProperties(
                            location = null,
                            rooms = null,
                            categoryId = i.id,
                            categoryName = i.name
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
    var properties = uiState.properties.reversed()
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
                Image(
                    painter = painterResource(id = R.drawable.no_image_icon_coming_soon),
                    contentDescription = "No image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .alpha(0.5f)
                        .height(140.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(
                    text = property.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = property.location.county
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = property.location.county.takeIf { property.location.county.length <= 6 } ?: "${property.location.county.substring(0, 4)}...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Light
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFa2a832)
                        )
                    ) {
                        Text(
                            text = property.category.takeIf { it.length <= 6 } ?: "${property.category.substring(0, 4)}...",
                            fontSize = 14.sp,
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
            viewModel = viewModel
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
            viewModel = viewModel
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
            navigateToSpecificProperty = {}
        )
    }
}