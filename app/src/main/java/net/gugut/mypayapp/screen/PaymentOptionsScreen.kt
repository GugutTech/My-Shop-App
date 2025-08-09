package net.gugut.mypayapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import net.gugut.mypayapp.R
import net.gugut.mypayapp.model.TShirt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentOptionsScreen(
    cartItems: Map<TShirt, Int>,
    totalPrice: Double,
    onGooglePayClick: () -> Unit,
    onCardPayClick: () -> Unit,
    onPayPalClick: () -> Unit,
    onEditCart: () -> Unit,
    onBack: () -> Unit,
    onContinueShopping: () -> Unit,
    onSignOut: () -> Unit,
    navController: NavController
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("T-Shirt Shop") },
                actions = {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Your Cart Summary",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // List cart items with image, name, quantity, and price
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems.toList()) { (item, qty) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = item.imageResourceId),
                            contentDescription = item.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(item.name, style = MaterialTheme.typography.bodyLarge)
                            Text("Qty: $qty", style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(
                            text = "$${"%.2f".format(item.price * qty)}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Total: $${"%.2f".format(totalPrice)}",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Payment methods
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onGooglePayClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.icons8_google_pay),
                        contentDescription = "Google Pay",
                        modifier = Modifier.size(64.dp),
                        tint = Color.Unspecified
                    )
                }

                IconButton(onClick = onPayPalClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.icons8_paypal),
                        contentDescription = "PayPal",
                        modifier = Modifier.size(64.dp),
                        tint = Color.Unspecified
                    )
                }

                IconButton(onClick = onCardPayClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.icons8_magnetic_card_24),
                        contentDescription = "Credit Card",
                        modifier = Modifier.size(64.dp),
                        tint = Color.Unspecified
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Continue Shopping
            OutlinedButton(
                onClick = onContinueShopping,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Continue Shopping")
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}






