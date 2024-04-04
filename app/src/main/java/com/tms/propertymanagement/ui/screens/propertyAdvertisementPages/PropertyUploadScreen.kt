package com.tms.propertymanagement.ui.screens.propertyAdvertisementPages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
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
import coil.compose.rememberImagePainter
import com.tms.propertymanagement.R
import com.tms.propertymanagement.ui.theme.PropEaseTheme

@Composable
fun PropertyUploadScreen(
    modifier: Modifier = Modifier
) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Upload Property",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(20.dp))
        PropertyFeaturesSelection()
        Spacer(modifier = Modifier.height(20.dp))
        PropertyDetails()
        Spacer(modifier = Modifier.height(20.dp))
        ImagesSelection()
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            shape = RoundedCornerShape(0.dp),
            onClick = { /*TODO*/ },
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

@Composable
fun PropertyDetails(
    modifier: Modifier = Modifier
) {
    val features = remember { mutableStateListOf<String>("") }
    Column {
        PropertyUploadInputForm(
            labelText = "Title",
            value = "",
            maxLines = 2,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
            onValueChanged = {},
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        PropertyUploadInputForm(
            labelText = "Description",
            value = "",
            maxLines = 4,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
            onValueChanged = {},
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Add Features i.e 'Free wifi'",
            fontWeight = FontWeight.Bold
        )
        features.forEachIndexed { index, s ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                PropertyUploadInputForm(
                    labelText = "Feature ${index + 1}",
                    value = features[index],
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Text
                    ),
                    onValueChanged = {
                        features[index] = it
                    }
                )
                IconButton(onClick = {
                    features.removeAt(index)

                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Remove this field"
                    )
                }
            }

        }
        IconButton(onClick = { features.add("") }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add new field"
            )
        }
    }
}

@Composable
fun ImagesSelection(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var imageUrl by remember {
        mutableStateOf<Uri?>(null)
    }

    val images = remember { mutableStateListOf<Uri>() }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {uri ->
            imageUrl = uri
            images.add(uri!!)
        }
    )

    Column {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
        ) {
            images.forEachIndexed { index, uri ->
                Row {
                    Card(
                        modifier = Modifier
                    ) {
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
                    }
                    IconButton(onClick = {
                        images.removeAt(index)
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
    modifier: Modifier = Modifier
) {
    Column() {
        Row {
            NumberOfRoomsSelection()
            Spacer(modifier = Modifier.weight(1f))
            CategorySelection()
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
    modifier: Modifier = Modifier
) {
    val rooms = listOf<Int>(1, 2, 3, 4, 5, 6, 7, 8)
    var selectedRoom by remember {
        mutableIntStateOf(0)
    }
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
                    text = "No. Rooms".takeIf { selectedRoom == 0 }
                        ?: "$selectedRoom room".takeIf { selectedRoom == 1 }
                        ?: "$selectedRoom rooms",
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
                        selectedRoom = i
                        expanded = !expanded
                    }
                )
            }
        }
    }
}

@Composable
fun CategorySelection(
    modifier: Modifier = Modifier
) {
    var categories = listOf<String>("Rental", "Airbnb", "On Sale", "Shop")
    var selectedCategory by remember {
        mutableStateOf("Rental")
    }
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
                    text = selectedCategory,
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
            categories.forEachIndexed { index, i ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = i
                        )
                    },
                    onClick = {
                        selectedCategory = i
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
    PropEaseTheme {
        ImagesSelection()
    }
}

@Preview(showBackground = true)
@Composable
fun PropertyUploadScreenPreview() {
    PropEaseTheme {
        PropertyUploadScreen()
    }
}