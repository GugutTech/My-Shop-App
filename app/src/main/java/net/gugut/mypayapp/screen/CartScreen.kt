package net.gugut.mypayapp.screen

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import net.gugut.mypayapp.model.TShirt
import net.gugut.mypayapp.viewModel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartViewModel: MainViewModel,
    navController: NavController,
    onBack: () -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val cartItems = cartViewModel.cartItems.collectAsState().value
    val savedItems = cartViewModel.savedItems.collectAsState().value
    val totalPrice = cartViewModel.getTotalPrice()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("T-Shirt Shop") },
                actions = {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (cartItems.isEmpty()) {
                Text("Your cart is empty.")
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cartItems.toList()) { (item, qty) ->
                        CartItemRow(
                            item = item,
                            qty = qty,
                            onAdd = { cartViewModel.addToCart(item) },
                            onRemove = { cartViewModel.removeFromCart(item) },
                            onSave = { cartViewModel.saveForLater(item) },
                            onShare = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Check out this item: ${item.name} for $${item.price}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share via"))
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Total: $${String.format("%.2f", totalPrice)}")

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { navController.navigate("payment") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Checkout")
                }
            }

            if (savedItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Saved for Later", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(savedItems.toList()) { (item, qty) ->
                        SavedItemRow(
                            item = item,
                            qty = qty,
                            onMoveToCart = { cartViewModel.moveToCartFromSaved(item) },
                            onRemove = { cartViewModel.removeSavedItem(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: TShirt,
    qty: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onSave: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(id = item.imageResourceId),
                    contentDescription = item.name,
                    modifier = Modifier.size(64.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(item.name, style = MaterialTheme.typography.titleMedium)
                    Text("$${item.price}", color = MaterialTheme.colorScheme.primary)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onRemove) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease")
                    }
                    Text(qty.toString(), modifier = Modifier.align(Alignment.CenterVertically))
                    IconButton(onClick = onAdd) {
                        Icon(Icons.Default.Add, contentDescription = "Increase")
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                OutlinedButton(onClick = onSave) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Save")
                    Spacer(Modifier.width(4.dp))
                    Text("Save for Later")
                }
                OutlinedButton(onClick = onShare) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
                    Spacer(Modifier.width(4.dp))
                    Text("Share")
                }
            }
        }
    }
}

@Composable
fun SavedItemRow(
    item: TShirt,
    qty: Int,
    onMoveToCart: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = item.imageResourceId),
                    contentDescription = item.name,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Qty: $qty",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Price: $${String.format("%.2f", item.price)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onMoveToCart,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Move to Cart"
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Move to Cart")
                }

                OutlinedButton(
                    onClick = onRemove,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove"
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Remove")
                }
            }
        }
    }
}