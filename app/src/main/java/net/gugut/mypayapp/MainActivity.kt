package net.gugut.mypayapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.gugut.mypayapp.nav.MainScaffold
import net.gugut.mypayapp.screen.LoginScreen
import net.gugut.mypayapp.screen.SignUpScreen
import net.gugut.mypayapp.screen.TShirtListScreen
import net.gugut.mypayapp.ui.theme.MyPayAppTheme
import net.gugut.mypayapp.viewModel.MainViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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