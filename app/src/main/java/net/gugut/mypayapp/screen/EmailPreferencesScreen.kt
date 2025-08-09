package net.gugut.mypayapp.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.*

@Composable
fun EmailPreferencesScreen(navController: NavController) {
    var promotionsEnabled by remember { mutableStateOf(true) }
    var updatesEnabled by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Email Preferences", style = MaterialTheme.typography.headlineSmall)

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = promotionsEnabled, onCheckedChange = { promotionsEnabled = it })
            Text("Receive promotional emails")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = updatesEnabled, onCheckedChange = { updatesEnabled = it })
            Text("Receive app updates")
        }

        Button(
            onClick = {
                Toast.makeText(context, "Preferences saved", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Preferences")
        }
    }
}
