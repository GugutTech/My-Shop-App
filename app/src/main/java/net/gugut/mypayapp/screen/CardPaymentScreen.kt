package net.gugut.mypayapp.screen

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.*
import net.gugut.mypayapp.model.TShirt
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import net.gugut.mypayapp.util.fetchCityStateFromZip
import net.gugut.mypayapp.util.formatCVV
import net.gugut.mypayapp.util.formatCardNumber
import net.gugut.mypayapp.util.formatExpiryDate
import net.gugut.mypayapp.util.formatInputWithCursor
import net.gugut.mypayapp.util.formatPhoneNumber
import net.gugut.mypayapp.util.getCardType
import net.gugut.mypayapp.util.isCVVValid
import net.gugut.mypayapp.util.isExpiryValid
import net.gugut.mypayapp.util.isValidCardNumber
import net.gugut.mypayapp.viewModel.MainViewModel
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CardPaymentScreen(
    viewModel: MainViewModel,
    cartItems: List<Pair<TShirt, Int>>,
    navController: NavController,
    onPayClicked: (cardNumber: String, expiry: String, cvv: String) -> Unit
) {
    // Keep TextFieldValue state for cursor & text management
    var cardNumberState by remember { mutableStateOf(TextFieldValue(viewModel.cardNumber)) }
    var expiryDateState by remember { mutableStateOf(TextFieldValue(viewModel.expiryDate)) }
    var cvvState by remember { mutableStateOf(TextFieldValue(viewModel.cvv)) }
    var phoneState by remember { mutableStateOf(TextFieldValue(viewModel.phone)) }

    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

    var showExitDialog by remember { mutableStateOf(false) }
    var pendingNavigation by remember { mutableStateOf<(() -> Unit)?>(null) }

    BackHandler(enabled = true) {
        showExitDialog = true
    }

    // Show confirmation dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Exit Payment") },
            text = { Text("If you leave now, you will have to start the payment process again. Are you sure?") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    pendingNavigation?.invoke() ?: navController.popBackStack() // Use stored nav or go back
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Shipping Information", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))

        // Test notifications
        Button(onClick = {
            viewModel.notifyOrderStatus(context, "shipped")
        }) {
            Text("Test Shipped Notification")
        }

        OutlinedTextField(
            value = viewModel.firstName,
            onValueChange = { viewModel.firstName = it },
            label = { Text("First Name*") },
            isError = viewModel.firstNameTouched && viewModel.firstName.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )
        if (viewModel.firstNameTouched && viewModel.firstName.isBlank()) {
            Text("First name is required", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        OutlinedTextField(
            value = viewModel.lastName,
            onValueChange = { viewModel.lastName = it },
            label = { Text("Last Name*") },
            isError = viewModel.lastNameTouched && viewModel.lastName.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )
        if (viewModel.lastNameTouched && viewModel.lastName.isBlank()) {
            Text("Last name is required", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        OutlinedTextField(
            value = phoneState,
            onValueChange = { newValue ->
                phoneState = formatInputWithCursor(phoneState, newValue.text, ::formatPhoneNumber)
                viewModel.phone = phoneState.text
                viewModel.phoneError = phoneState.text.filter { it.isDigit() }.length != 10
                if (!viewModel.phoneTouched) viewModel.phoneTouched = true
            },
            label = { Text("Phone Number*") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = viewModel.phoneTouched && viewModel.phoneError,
            modifier = Modifier.fillMaxWidth()
        )
        if (viewModel.phoneTouched && viewModel.phoneError) {
            Text("Phone number must be exactly 10 digits", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        OutlinedTextField(
            value = viewModel.address,
            onValueChange = {
                viewModel.address = it
                if (!viewModel.addressTouched) viewModel.addressTouched = true
            },
            label = { Text("Delivery Address*") },
            isError = viewModel.addressTouched && viewModel.address.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )
        if (viewModel.addressTouched && viewModel.address.isBlank()) {
            Text("Delivery address is required", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        OutlinedTextField(
            value = viewModel.zipCode,
            onValueChange = {
                viewModel.onZipCodeChanged(it)
                if (!viewModel.zipCodeTouched) viewModel.zipCodeTouched = true
            },
            label = { Text("ZIP Code*") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = viewModel.zipCodeTouched && viewModel.zipCode.length != 5,
            modifier = Modifier.fillMaxWidth()
        )
        if (viewModel.zipCodeTouched && viewModel.zipCode.length != 5) {
            Text("ZIP Code must be 5 digits", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        OutlinedTextField(
            value = viewModel.city,
            onValueChange = {},
            label = { Text("City") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = viewModel.state,
            onValueChange = {},
            label = { Text("State") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        Divider()

        Text("Item Summary", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            cartItems.forEach { (tshirt, quantity) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = tshirt.imageResourceId),
                        contentDescription = tshirt.name,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(tshirt.name, style = MaterialTheme.typography.bodyLarge)
                        Text("Qty: $quantity", style = MaterialTheme.typography.bodyMedium)
                        Text("Delivery: TBD", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Divider()

        Text("Payment Information", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = cardNumberState,
            onValueChange = { newValue ->
                cardNumberState = formatInputWithCursor(cardNumberState, newValue.text, ::formatCardNumber)
                viewModel.cardNumber = cardNumberState.text
                viewModel.cardType = getCardType(cardNumberState.text)
                viewModel.cardNumberError = !isValidCardNumber(cardNumberState.text)
                viewModel.cvvError = !isCVVValid(viewModel.cvv, viewModel.cardType)
            },
            label = { Text("Card Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = viewModel.cardNumberError && cardNumberState.text.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )
        if (viewModel.cardNumberError && cardNumberState.text.isNotEmpty()) {
            Text("Invalid card number", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        OutlinedTextField(
            value = expiryDateState,
            onValueChange = { newValue ->
                expiryDateState = formatInputWithCursor(expiryDateState, newValue.text, ::formatExpiryDate)
                viewModel.expiryDate = expiryDateState.text
                viewModel.expiryDateError = !isExpiryValid(expiryDateState.text)
            },
            label = { Text("Expiry Date (MM/YY)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = viewModel.expiryDateError && expiryDateState.text.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )
        if (viewModel.expiryDateError && expiryDateState.text.isNotEmpty()) {
            Text("Invalid or expired date", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        OutlinedTextField(
            value = cvvState,
            onValueChange = { newValue ->
                cvvState = formatInputWithCursor(cvvState, newValue.text) { input ->
                    formatCVV(input, viewModel.cardNumber)
                }
                viewModel.cvv = cvvState.text
                viewModel.cvvError = !isCVVValid(cvvState.text, viewModel.cardType)
            },
            label = { Text("CVV") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            isError = viewModel.cvvError && cvvState.text.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )
        if (viewModel.cvvError && cvvState.text.isNotEmpty()) {
            Text("Invalid CVV", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cancel Payment button
            OutlinedButton(
                onClick = {
                    pendingNavigation = { navController.popBackStack() }
                    showExitDialog = true
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel Payment")
            }

            Button(
                enabled = true, // <- force it to be always enabled
                onClick = {
                    onPayClicked("1234567812345678", "12/26", "123") // fake data
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Continue to Pay")
            }

            // Continue to Pay button
//            Button(
//                enabled = viewModel.isFormValid(),
//                onClick = {
//                    Log.d("CardPayment", "Continue to Pay clicked")
//                    viewModel.startPayment(cartItems.toMap()) {
//                        navController.navigate("confirmationScreen")
//                    }
//                },
//                modifier = Modifier.weight(1f)
//            ) {
//                Text("Continue to Pay")
//            }
        }
    }
}