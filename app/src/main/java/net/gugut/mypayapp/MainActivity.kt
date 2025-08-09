package net.gugut.mypayapp

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.stripe.android.paymentsheet.PaymentSheet
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
import net.gugut.mypayapp.screen.TShirtListScreen
import net.gugut.mypayapp.screen.UpdateProfileScreen
import net.gugut.mypayapp.ui.theme.MyPayAppTheme
import net.gugut.mypayapp.viewModel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val mainViewModel: MainViewModel = viewModel()

            MyPayAppTheme {
                if (mainViewModel.isUserLoggedIn) {
                    MainScaffold(
                        mainViewModel = mainViewModel,
                        onSignOut = {
                            mainViewModel.logoutUser()
                        }
                    )
                } else {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            LoginScreen(
                                navController = navController,
                                mainViewModel = mainViewModel,
                                username = "username"
                            )
                        }
                        composable("signup") {
                            SignUpScreen(
                                navController = navController,
                                mainViewModel = mainViewModel,
                                username = "username"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyPayAppTheme {
        TShirtListScreen(
            onViewCart = {},
            onAddToCart = {},
            navController = rememberNavController()
        )
    }
}