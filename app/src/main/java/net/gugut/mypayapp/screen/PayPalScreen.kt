//package net.gugut.mypayapp.screen
//
//import android.app.Activity
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//
//@RequiresApi(Build.VERSION_CODES.M)
//@Composable
//fun PayPalScreen(
//    totalPrice: Double,
//    onPaymentApproved: () -> Unit,
//    onPaymentCanceled: () -> Unit,
//    onPaymentError: (String) -> Unit
//) {
//    val context = LocalContext.current
//    val activity = context as Activity
//
//    // Format price to two decimals
//    val formattedAmount = remember(totalPrice) {
//        String.format("%.2f", totalPrice)
//    }
//
//    // Register callbacks on composition
//    DisposableEffect(Unit) {
//        PayPalCheckout.registerCallbacks(
//            onApprove = OnApprove { approval ->
//                approval.orderActions.capture { captureResult ->
//                    if (captureResult != null) {
//                        onPaymentApproved()
//                    } else {
//                        onPaymentError("Failed to capture payment")
//                    }
//                }
//            },
//            onCancel = OnCancel {
//                onPaymentCanceled()
//            },
//            onError = OnError { errorInfo ->
//                onPaymentError(errorInfo.reason)
//            }
//        )
//
//        onDispose {
//            // Cleanup if needed
//        }
//    }
//
//    // Launch checkout once when entering composition
//    LaunchedEffect(Unit) {
//        PayPalCheckout.startCheckout(
//            createOrder = CreateOrder { createOrderActions ->
//                val order = Order(
//                    intent = OrderIntent.CAPTURE,
//                    appContext = AppContext(userAction = UserAction.PAY_NOW),
//                    purchaseUnitList = listOf(
//                        PurchaseUnit(
//                            amount = Amount(
//                                currencyCode = CurrencyCode.USD,
//                                value = formattedAmount
//                            )
//                        )
//                    )
//                )
//                createOrderActions.create(order)
//            }
//        )
//    }
//
//    // UI feedback while redirecting
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        CircularProgressIndicator()
//        Spacer(modifier = Modifier.height(16.dp))
//        Text("Redirecting to PayPal Checkout...")
//    }
//}
//
