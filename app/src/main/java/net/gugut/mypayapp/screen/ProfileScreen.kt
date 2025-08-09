package net.gugut.mypayapp.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.gugut.mypayapp.viewModel.MainViewModel

@Composable
fun ProfileScreen(
    mainViewModel: MainViewModel,
    navController: NavController,
    onSignOut: () -> Unit
) {
    val user by mainViewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Account", style = MaterialTheme.typography.headlineSmall)

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

