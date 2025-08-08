package net.gugut.mypayapp.screen

import android.app.Activity
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import net.gugut.mypayapp.payment.GooglePayUtils
import net.gugut.mypayapp.viewModel.MainViewModel

@Composable
fun GooglePayScreen(
    totalPrice: Double,
    paymentViewModel: MainViewModel,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity
    val paymentsClient = GooglePayUtils.createPaymentsClient(context, activity)

    val success by paymentViewModel.paymentSuccess.collectAsState()
    val error by paymentViewModel.paymentError.collectAsState()

    // Launcher to handle the result of Google Pay flow
    val launcher = rememberLauncherForActivityResult(StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val paymentData = PaymentData.getFromIntent(result.data ?: return@rememberLauncherForActivityResult)
            val json = paymentData?.toJson()
            println("✅ Google Pay Success: $json")
            paymentViewModel.handlePaymentSuccess()
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            println("❌ Google Pay canceled by user")
            paymentViewModel.handlePaymentFailure("Payment canceled by user")
        } else {
            val errorMessage = result.data?.extras?.getString("com.google.android.gms.wallet.EXTRA_ERROR_CODE")
            println("❌ Google Pay Failed: $errorMessage")
            paymentViewModel.handlePaymentFailure(errorMessage ?: "Unknown error")
        }
    }

    // Check if Google Pay is ready
    LaunchedEffect(Unit) {
        val isReadyJson = GooglePayUtils.getIsReadyToPayRequest()
        val request = IsReadyToPayRequest.fromJson(isReadyJson.toString())
        paymentsClient.isReadyToPay(request).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result == true) {
                    // Launch Google Pay UI
                    val formattedPrice = String.format("%.2f", totalPrice)
                    val paymentRequest = GooglePayUtils.getPaymentDataRequest(formattedPrice)
                    paymentsClient.loadPaymentData(paymentRequest).addOnSuccessListener { paymentData ->
                        val json = paymentData.toJson()
                        println("✅ Google Pay Immediate Success: $json")
                        paymentViewModel.handlePaymentSuccess()
                    }.addOnFailureListener { exception ->
                        if (exception is ResolvableApiException) {
                            val intentSender = exception.resolution.intentSender
                            launcher.launch(IntentSenderRequest.Builder(intentSender).build())
                        } else {
                            paymentViewModel.handlePaymentFailure(exception.localizedMessage ?: "Unknown error")
                        }
                    }
                } else {
                    paymentViewModel.handlePaymentFailure("Google Pay is not available on this device")
                }
            } else {
                paymentViewModel.handlePaymentFailure("Google Pay readiness check failed")
            }
        }
    }

    if (success) {
        onDone()
        paymentViewModel.resetPaymentState()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (error != null) {
            Text("Error: $error", color = Color.Red)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                paymentViewModel.resetPaymentState()
                onDone()
            }) {
                Text("Back")
            }
        } else if (!success) {
            CircularProgressIndicator()
        }
    }
}

