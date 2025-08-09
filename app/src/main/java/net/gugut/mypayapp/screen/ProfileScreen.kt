package net.gugut.mypayapp.screen

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

