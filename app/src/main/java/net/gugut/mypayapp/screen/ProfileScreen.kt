package net.gugut.mypayapp.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import net.gugut.mypayapp.viewModel.MainViewModel

@Composable
fun ProfileScreen(
    mainViewModel: MainViewModel,
    navController: NavController,
    onSignOut: () -> Unit
) {
    val user by mainViewModel.currentUser.collectAsState()
    val greeting by mainViewModel.greeting.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Greeting text at the top
        Text(
            text = if (user != null && user!!.fullName.isNotBlank()) {
                "$greeting, ${user!!.fullName} 👋"
            } else if (user != null) {
                "$greeting, ${user!!.username} 👋"
            } else {
                "$greeting, Guest 👋"
            },
            style = MaterialTheme.typography.headlineSmall
        )

        ProfileOption("👤  Update Account Info") {
            navController.navigate("updateProfile")
        }

        ProfileOption("🔒  Change Password") {
            navController.navigate("changePassword")
        }

        ProfileOption("📧  Manage Email Preferences") {
            navController.navigate("emailPreferences")
        }

        ProfileOption("📦  Order History") {
            navController.navigate("orderHistory")
        }

        Divider()

        Text(
            text = "Sign Out",
            color = Color.Red,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onSignOut)
                .padding(vertical = 8.dp)
        )
    }
}

@Composable
fun ProfileOption(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    )
}

