package net.gugut.mypayapp.nav

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import net.gugut.mypayapp.screen.AiChatScreen
import net.gugut.mypayapp.screen.CartScreen
import net.gugut.mypayapp.screen.TShirtListScreen
import net.gugut.mypayapp.viewModel.MainViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.navArgument
import net.gugut.mypayapp.R
import net.gugut.mypayapp.screen.CardPaymentScreen
import net.gugut.mypayapp.screen.ChangePasswordScreen
import net.gugut.mypayapp.screen.ConfirmationScreen
import net.gugut.mypayapp.screen.EmailPreferencesScreen
import net.gugut.mypayapp.screen.GooglePayScreen
import net.gugut.mypayapp.screen.OrderHistoryScreen
import net.gugut.mypayapp.screen.PayPalCheckoutScreen
import net.gugut.mypayapp.screen.PaymentOptionsScreen
import net.gugut.mypayapp.screen.ProfileScreen
import net.gugut.mypayapp.screen.TShirtDetailBottomSheet
import net.gugut.mypayapp.screen.UpdateProfileScreen

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScaffold(
    mainViewModel: MainViewModel,
    onSignOut: () -> Unit
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Only show bottom nav on these routes
    val bottomNavRoutes = listOf("home", "cart", "chat")
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentDestination in bottomNavRoutes) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                TShirtListScreen(
                    navController = navController,
                    onAddToCart = { mainViewModel.addToCart(it) },
                    onViewCart = { navController.navigate("cart") }
                )
            }


            composable("cart") {
                CartScreen(
                    cartViewModel = mainViewModel,
                    onBack = { navController.popBackStack() },
                    navController = navController,
                    onSignOut = onSignOut
                )
            }

            composable("chat") {
                AiChatScreen(
                    navController = navController
                )
            }

            composable("profile") {
                ProfileScreen(
                    mainViewModel = mainViewModel,
                    onSignOut = onSignOut,
                    navController = navController
                )
            }

            composable("orderHistory") {
                OrderHistoryScreen(navController)
            }

            composable("emailPreferences") {
                EmailPreferencesScreen(navController)
            }

            composable("changePassword") {
                ChangePasswordScreen(navController, mainViewModel)
            }

            composable("updateProfile") {
                UpdateProfileScreen(navController, mainViewModel)
            }

            composable("payment") {
                PaymentOptionsScreen(
                    cartItems = mainViewModel.cartItems.collectAsState().value,
                    totalPrice = mainViewModel.getTotalPrice(),
                    onGooglePayClick = { navController.navigate("googlePay") },
                    onCardPayClick = { navController.navigate("cardPayment") },
                    onPayPalClick = { navController.navigate("paypalPayment") },
                    onEditCart = { navController.navigate("cart") },
                    onBack = { navController.popBackStack() },
                    onContinueShopping = { navController.navigate("home") },
                    onSignOut = onSignOut,
                    navController = navController
                )
            }

            composable("cardPayment") {
                val context = LocalContext.current
                val cartItemsMap = mainViewModel.cartItems.collectAsState().value

                if (cartItemsMap.isNotEmpty()) {
                    CardPaymentScreen(
                        viewModel = mainViewModel,
                        cartItems = cartItemsMap.entries.map { it.toPair() },
                        navController = navController,
                        onPayClicked = { cardNumber, expiry, cvv ->
                            mainViewModel.notifyOrderStatus(context, "confirmed")
                            navController.navigate("confirmationScreen")
                        }
                    )
                } else {
                    Text(
                        "Your cart is empty",
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            composable("googlePay/{amount}") { backStackEntry ->
                val amount = backStackEntry.arguments?.getString("amount")?.toDoubleOrNull() ?: 0.0
                GooglePayScreen(
                    totalPrice = amount,
                    paymentViewModel = mainViewModel,
                    onDone = {
                        navController.navigate("confirmationScreen")
                    }
                )
            }

//            composable("googlePay") {
//                GooglePayScreen(
//                    totalPrice = mainViewModel.getTotalPrice(),
//                    paymentViewModel = mainViewModel,
//                    onDone = {
//                        navController.navigate("confirmationScreen") {
//                            popUpTo("cart") { inclusive = true }
//                        }
//                    }
//                )
//            }
            composable(
                route = "paypalPayment/{amount}",
                arguments = listOf(navArgument("amount") {
                    type = NavType.StringType
                    defaultValue = "0_00" // Use underscore as default
                })
            ) { backStackEntry ->
                val rawAmount = backStackEntry.arguments?.getString("amount") ?: "0_00"
                val amount = rawAmount.replace('_', '.') // Convert back to decimal string

                PayPalCheckoutScreen(
                    clientID = "AfQc_3M9KGo9lRgzu_qqbUoEC0GGb11H-FW5WU0GS9EXJWVGOz9Ltx4tDdmb7uY5y3v5qryCsnwAHze5",
                    secretID = "EPJyn1ZS2yOZEInlhO1bb5qnl_NkBfU4q_di8O8GQpFCEBOAH99xZBJloPincgXFbpMBz6kr383A9LVw",
                    amount = amount,
                    onPaymentSuccess = {
                        mainViewModel.notifyOrderStatus(context, "confirmed")
                        navController.navigate("confirmationScreen")
                    },
                    onPaymentCanceled = { /* handle cancel */ },
                    onPaymentError = { error -> /* handle error */ }
                )
            }


//            composable("paypalPayment") {
//                val context = LocalContext.current
//                PayPalCheckoutScreen(
//                    clientID = "YOUR_CLIENT_ID",
//                    secretID = "YOUR_SECRET_ID",
//                    onPaymentSuccess = {
//                        mainViewModel.notifyOrderStatus(context, "confirmed")
//                        navController.navigate("confirmationScreen")
//                    },
//                    onPaymentCanceled = {
//                        // Handle payment cancellation
//                    },
//                    onPaymentError = { errorMessage ->
//                        // Handle payment error
//                    }
//                )
//            }
            composable("confirmationScreen") {
                ConfirmationScreen(
                    navController = navController,
                    cartItems = mainViewModel.cartItems.collectAsState().value,
                    totalPrice = mainViewModel.getTotalPrice(),
                    confirmationNumber = mainViewModel.confirmationNumber.value,
                    shippingAddress = mainViewModel.shippingAddress
                )
            }
            composable("bottomSheet") {
                TShirtDetailBottomSheet(
                    baseName = "Base Name",
                    colorToImageRes = mapOf("Black" to R.drawable.tshirt_black),
                    colors = listOf("Black"),
                    sizes = listOf("S", "M", "L"),

                    sizePrices = mapOf("S" to 10.0, "M" to 12.0, "L" to 14.0),
                    colorPrices = mapOf("Black" to 5.0),

                    onAddToCart = { mainViewModel.addToCart(it) },
                    onDismiss = { navController.popBackStack() }
                )

            }
        }
    }
}
