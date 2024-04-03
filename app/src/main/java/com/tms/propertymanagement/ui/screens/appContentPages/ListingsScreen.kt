package com.tms.propertymanagement.ui.screens.appContentPages

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tms.propertymanagement.R
import com.tms.propertymanagement.ui.theme.PropEaseTheme

@Composable
fun ListingsScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        ListingsFilterSection()
    }
}

@Composable
fun ListingsFilterSection(
    modifier: Modifier = Modifier
) {
    Column {
        LocationSearchForm(
            leadingIcon = painterResource(id = R.drawable.locations),
            labelText = "Location",
            value = "",
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text,
            ),
            onValueChanged = {},
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            NumberOfRoomsSelection()
            Spacer(modifier = Modifier.width(20.dp))
            CategorySelection()
        }
        Spacer(modifier = Modifier.height(16.dp))
        ListingItems()
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
    val categories = listOf<String>("Rental", "Airbnb", "On Sale", "Shop")
    var selectedCategory by remember {
        mutableStateOf("")
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
                    text = "Category".takeIf { selectedCategory.isEmpty() } ?: selectedCategory,
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

@Composable
fun ListingItems(
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2)
    ) {
        items(10) {
            ListingItem(
                modifier = Modifier
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun ListingItem(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.sample_property),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color =
                        Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(
                    text = "Gracious Apartment in Nairobi",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
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
                        contentDescription = "Nairobi"
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Nairobi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Light
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFa2a832)
                        )
                    ) {
                        Text(
                            text = "Rental",
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
    PropEaseTheme {
        ListingItem()
    }
}

@Preview(showBackground = true)
@Composable
fun NumberOfRoomsSelectionPreview(
    modifier: Modifier = Modifier
) {
    PropEaseTheme {
        NumberOfRoomsSelection()
    }
}

@Preview(showBackground = true)
@Composable
fun ListingsFilterSectionPreview(
    modifier: Modifier = Modifier
) {
    PropEaseTheme {
        ListingsFilterSection()
    }
}

@Preview(showBackground = true)
@Composable
fun ListingsScreenPreview(
    modifier: Modifier = Modifier
) {
    PropEaseTheme {
        ListingsScreen()
    }
}