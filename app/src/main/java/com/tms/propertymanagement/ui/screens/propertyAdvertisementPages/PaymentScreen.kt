package com.tms.propertymanagement.ui.screens.propertyAdvertisementPages

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.propertymanagement.tms.PropEaseViewModelFactory
import com.propertymanagement.tms.apiModel.PropertyData
import com.propertymanagement.tms.nav.NavigationDestination
import com.propertymanagement.tms.ui.theme.PropEaseTheme
import com.propertymanagement.tms.utils.ReusableFunctions
import com.tms.propertymanagement.utils.PaymentStatus
import com.tms.propertymanagement.utils.PaymentStatusCheck
import kotlinx.coroutines.delay

object PaymentScreenDestination: NavigationDestination{
    override val title: String = "Payment screen"
    override val route: String = "payment-screen"
    val propertyId: String = "propertyId"
    val routeWithArgs: String = "$route/{$propertyId}"
}

val propertyName = "Three bedroom rental unit in Nairobi"

@Composable
fun PaymentScreenComposable(
    navigateToPreviousScreen: () -> Unit,
    navigateToHomeScreenWithArgs: (childScreen: String) -> Unit,
) {
    BackHandler(onBack = navigateToPreviousScreen)
    val context = LocalContext.current
    val viewModel: PaymentScreenViewModel = viewModel(factory = PropEaseViewModelFactory.Factory)
    val uiState by viewModel.uiState.collectAsState()

    var i by remember {
        mutableIntStateOf(0)
    }

    var showFailureToastMessage by remember {
        mutableStateOf(true)
    }

    var processingPayment by remember {
        mutableStateOf(false)
    }
    
    var timeLeft by remember {
        mutableIntStateOf(60)
    }

    if(uiState.paymentStatus == PaymentStatus.SUCCESS) {
        processingPayment = true
        LaunchedEffect(Unit) {
            while (timeLeft > 0 && processingPayment) {
                delay(1000)
                timeLeft--
            }
            if(timeLeft == 0) {
                timeLeft = 60
                viewModel.getPaymentStatus()
                viewModel.resetPaymentStatus()
            }

        }

    } else if(uiState.paymentStatus == PaymentStatus.INITIAL) {
        processingPayment = false
    }

    if(uiState.paymentStatusCheck == PaymentStatusCheck.SUCCESS) {
        if(uiState.paymentSuccessful) {
            Toast.makeText(context, "Payment successful", Toast.LENGTH_SHORT).show()
            navigateToHomeScreenWithArgs("advertisement-screen")
            viewModel.resetPaymentStatusCheck()
        } else if(!uiState.paymentSuccessful && showFailureToastMessage) {
            Toast.makeText(context, "Payment unsuccessful. Try again", Toast.LENGTH_SHORT).show()
            showFailureToastMessage = false
            viewModel.resetPaymentStatusCheck()
        }

    }

    Box {
        PaymentScreen(
            propertyName = uiState.property.title,
            owner = "${uiState.property.user.fname}, ${uiState.property.user.lname}",
            navigateToPreviousScreen = {
                viewModel.resetPaymentStatus()
                navigateToPreviousScreen()
            },
            onInitializePayment = {
                viewModel.payForPropertyAd()
                processingPayment = true
            },
            paymentStatus = uiState.paymentStatus,
            processingPayment = processingPayment,
            paymentSuccessful = uiState.paymentSuccessful,
            timeLeft = timeLeft
        )
    }
}

@Composable
fun PaymentScreen(
    propertyName: String,
    owner: String,
    navigateToPreviousScreen: () -> Unit,
    onInitializePayment: () -> Unit,
    paymentStatus: PaymentStatus,
    processingPayment: Boolean,
    paymentSuccessful: Boolean,
    timeLeft: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = navigateToPreviousScreen) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Previous screen"
                )
            }
        }

        PaymentCard(
            propertyName = propertyName,
            owner = owner,
        )
        Spacer(modifier = Modifier.weight(1f))
        if(processingPayment) {
            Button(
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = onInitializePayment
            ) {
                Text(text = "Processing $timeLeft")

            }
        } else {
            Button(
                enabled = paymentStatus != PaymentStatus.LOADING || !paymentSuccessful,
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = onInitializePayment
            ) {
                if(paymentStatus == PaymentStatus.LOADING) {
                    CircularProgressIndicator()
                } else {
                    Text(text = "Pay")
                }

            }
        }


    }
}

@Composable
fun PaymentCard(
    propertyName: String,
    owner: String,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Text(
                text = propertyName,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Owner: ",
                    fontWeight = FontWeight.Bold
                )
                Text(text = owner)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Payable amount: ",
                    fontWeight = FontWeight.Bold
                )
                Text(text = ReusableFunctions.formatMoneyValue(1.00))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentScreenPreview() {
    PropEaseTheme {
        PaymentScreen(
            propertyName = propertyName,
            owner = "Alex Mbogo",
            navigateToPreviousScreen = {},
            onInitializePayment = {},
            paymentStatus = PaymentStatus.INITIAL,
            processingPayment = false,
            paymentSuccessful = false,
            timeLeft = 20
        )
    }
}