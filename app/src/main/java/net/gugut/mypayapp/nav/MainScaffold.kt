package net.gugut.mypayapp.nav

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import net.gugut.mypayapp.screen.AiChatScreen
import net.gugut.mypayapp.screen.CartScreen
import net.gugut.mypayapp.screen.TShirtListScreen
import net.gugut.mypayapp.viewModel.MainViewModel
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.stripe.android.paymentsheet.PaymentSheet
import net.gugut.mypayapp.R
import net.gugut.mypayapp.model.TShirt
import net.gugut.mypayapp.nav.MainScaffold
import net.gugut.mypayapp.screen.CardPaymentScreen
import net.gugut.mypayapp.screen.CartScreen
import net.gugut.mypayapp.screen.ChangePasswordScreen
import net.gugut.mypayapp.screen.ConfirmationScreen
import net.gugut.mypayapp.screen.EmailPreferencesScreen
import net.gugut.mypayapp.screen.GooglePayScreen
import net.gugut.mypayapp.screen.LoginScreen
import net.gugut.mypayapp.screen.OrderHistoryScreen
//import net.gugut.mypayapp.screen.PayPalScreen
import net.gugut.mypayapp.screen.PaymentOptionsScreen
import net.gugut.mypayapp.screen.ProfileScreen
import net.gugut.mypayapp.screen.SignUpScreen
import net.gugut.mypayapp.screen.TShirtDetailBottomSheet
import net.gugut.mypayapp.screen.TShirtListScreen
import net.gugut.mypayapp.screen.UpdateProfileScreen
import net.gugut.mypayapp.ui.theme.MyPayAppTheme


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScaffold(
    mainViewModel: MainViewModel,
    onSignOut: () -> Unit
) {
    val navController = rememberNavController()

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
