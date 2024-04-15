package com.propertymanagement.tms.ui.screens.propertyAdvertisementPages

import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.propertymanagement.tms.PropEaseViewModelFactory
import com.propertymanagement.tms.R
import com.propertymanagement.tms.ui.theme.PropEaseTheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PropertyUploadScreen(
    navigateToListingsScreen: () -> Unit,
    navigateToPreviousPage: () -> Unit,
    navigateToHomeScreenWithArguments: (childScreen: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: PropertyUploadScreenViewModel = viewModel(factory = PropEaseViewModelFactory.Factory)
    val uiState by viewModel.uiState.collectAsState()

    var showPreviewScreen by rememberSaveable {
        mutableStateOf(false)
    }

    BackHandler(onBack = navigateToPreviousPage)

    if(showPreviewScreen) {
        PropertyUploadPreviewScreen(
            viewModel = viewModel,
            uiState = uiState,
            navigateToPreviousScreen = { showPreviewScreen = !showPreviewScreen },
            navigateToHomeScreenWithArguments = {
                navigateToHomeScreenWithArguments(it)
            }
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    navigateToPreviousPage()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Navigate to previous page"
                    )
                }
                Text(
                    text = "Upload Property",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                PropertyFeaturesSelection(
                    viewModel = viewModel,
                    uiState = uiState
                )
                Spacer(modifier = Modifier.height(20.dp))
                PropertyDetails(
                    viewModel = viewModel,
                    uiState = uiState
                )
                Spacer(modifier = Modifier.height(20.dp))
                ImagesSelection(
                    viewModel = viewModel,
                    uiState = uiState
                )
                Spacer(modifier = Modifier.height(40.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    ),
                    shape = RoundedCornerShape(0.dp),
                    onClick = {
                        viewModel.checkIfAllFieldsAreFilled()
                        showPreviewScreen = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "Preview")
                    Icon(
                        painter = painterResource(id = R.drawable.preview),
                        contentDescription = "Preview Changes"
                    )
                }
            }

        }
    }


}

@Composable
fun PropertyDetails(
    viewModel: PropertyUploadScreenViewModel,
    uiState: PropertyUploadScreenUiState,
    modifier: Modifier = Modifier
) {
    Column {
        PropertyUploadInputForm(
            labelText = "Title",
            value = uiState.title,
            maxLines = 2,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
            onValueChanged = {
                             viewModel.updateTitle(it)
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        PropertyUploadInputForm(
            labelText = "Description",
            value = uiState.description,
            maxLines = 4,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
            onValueChanged = {
                             viewModel.updateDescription(it)
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        PropertyUploadInputForm(
            labelText = "Price",
            value = uiState.price,
            maxLines = 1,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Decimal
            ),
            onValueChanged = {
                viewModel.updatePrice(it)
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            PropertyUploadInputForm(
                labelText = "County",
                value = uiState.county,
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                ),
                onValueChanged = {
                    viewModel.updateCounty(it)
                },
                modifier = Modifier
                    .weight(1f)
            )
            Spacer(modifier = Modifier.width(20.dp))
            PropertyUploadInputForm(
                labelText = "Address / town",
                value = uiState.address,
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                ),
                onValueChanged = {
                    viewModel.updateAddress(it)
                },
                modifier = Modifier
                    .weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Add Features i.e 'Free wifi'",
            fontWeight = FontWeight.Bold
        )
        uiState.features.forEachIndexed { index, s ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                PropertyUploadInputForm(
                    labelText = "Feature ${index + 1}",
                    value = uiState.features[index],
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Text
                    ),
                    onValueChanged = {
                        viewModel.updateFeature(index, it)
                    }
                )
                IconButton(onClick = {
                    viewModel.removeFeatureField(index)

                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Remove this field"
                    )
                }
            }

        }
        IconButton(onClick = { viewModel.addFeatureField() }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add new field"
            )
        }

    }
}

@Composable
fun ImagesSelection(
    viewModel: PropertyUploadScreenViewModel,
    uiState: PropertyUploadScreenUiState,
    modifier: Modifier = Modifier
) {
    val images = remember { mutableStateListOf<Uri>() }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = {uriList ->
            if(uriList.isNotEmpty()) {
                for(uri in uriList) {
                    images.add(uri)
                    viewModel.uploadPhoto(uri)
                }
            }

        }
    )

    Column {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
        ) {
            uiState.images.forEachIndexed { index, uri ->
                Row {
                    Image(
                        rememberImagePainter(data = uri),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(
                                top = 5.dp,
                                end = 3.dp,
                                bottom = 5.dp
                            )
                            .size(100.dp)
                    )
                    IconButton(onClick = {
                        viewModel.removePhoto(index)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable {
                    galleryLauncher.launch("image/*")
                }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)

            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.upload),
                        contentDescription = null
                    )
                    Text(
                        text = "Click to upload images"
                    )
                }
            }
        }
    }
}

@Composable
fun PropertyFeaturesSelection(
    viewModel: PropertyUploadScreenViewModel,
    uiState: PropertyUploadScreenUiState,
    modifier: Modifier = Modifier
) {
    Column() {
        Row {
            NumberOfRoomsSelection(
                viewModel = viewModel,
                uiState = uiState
            )
            Spacer(modifier = Modifier.weight(1f))
            CategorySelection(
                viewModel = viewModel,
                uiState = uiState
            )
        }
    }
}

@Composable
fun PropertyUploadInputForm(
    labelText: String,
    value: String,
    maxLines: Int,
    keyboardOptions: KeyboardOptions,
    onValueChanged: (newValue: String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        shape = RoundedCornerShape(10.dp),

        label = {
            Text(text = labelText)
        },
        maxLines = maxLines,
        value = value,
        keyboardOptions = keyboardOptions,
        onValueChange = onValueChanged,
        modifier = modifier
    )
}

@Composable
fun NumberOfRoomsSelection(
    viewModel: PropertyUploadScreenViewModel,
    uiState: PropertyUploadScreenUiState,
    modifier: Modifier = Modifier
) {
    val rooms = listOf<Int>(1, 2, 3, 4, 5, 6, 7, 8)
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
                    text = "No. Rooms".takeIf { uiState.numberOfRooms == 0 }
                        ?: "${uiState.numberOfRooms} room".takeIf { uiState.numberOfRooms == 1 }
                        ?: "${uiState.numberOfRooms} rooms",
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
                        viewModel.updateNumberOfRoomsSelected(i)
                        expanded = !expanded
                    }
                )
            }
        }
    }
}

@Composable
fun CategorySelection(
    viewModel: PropertyUploadScreenViewModel,
    uiState: PropertyUploadScreenUiState,
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
                    text = uiState.category.name.takeIf { it.isNotEmpty() } ?: "Category",
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
                        viewModel.updateCategoryType(i)
                        expanded = !expanded
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImagesSelectionPreview() {
    val viewModel: PropertyUploadScreenViewModel = viewModel(factory = PropEaseViewModelFactory.Factory)
    val uiState by viewModel.uiState.collectAsState()
    PropEaseTheme {
        ImagesSelection(
            viewModel = viewModel,
            uiState = uiState
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PropertyUploadScreenPreview() {
    PropEaseTheme {
        PropertyUploadScreen(
            navigateToListingsScreen = {},
            navigateToPreviousPage = {},
            navigateToHomeScreenWithArguments = {}
        )
    }
}