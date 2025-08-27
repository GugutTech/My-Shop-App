package net.gugut.mypayapp.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import net.gugut.mypayapp.R
import net.gugut.mypayapp.model.TShirt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.gugut.mypayapp.model.ApiModuleItem
import net.gugut.mypayapp.model.Kit
import net.gugut.mypayapp.network.LocalDataLoader
import net.gugut.mypayapp.network.RetrofitInstance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TShirtListScreen(
    onAddToCart: (TShirt) -> Unit,
    navController: NavController
) {
    var teams by remember { mutableStateOf(emptyList<ApiModuleItem>()) }
    var selectedKit by remember { mutableStateOf<Kit?>(null) }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) {
        try {
            val apiResult = try {
                RetrofitInstance.api.getShirts()
            } catch (e: Exception) {
                emptyList()
            }
            teams = apiResult.ifEmpty {
                LocalDataLoader.loadShirtsData(context)
            }
        } catch (e: Exception) {
            teams = emptyList()
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading...")
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            teams.forEach { team ->
                item {
                    Text(
                        team.teamName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                val kitRows = team.kits.chunked(3)
                kitRows.forEach { rowKits ->
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            rowKits.forEach { kit ->
                                TShirtItem(
                                    baseName = kit.kitType,
                                    colorName = kit.season,
                                    imageUrl = kit.imageUrl,
                                    comingSoon = kit.comingSoon,
                                    price = kit.price,
                                    inStock = kit.inStock,
                                    onClick = {
                                        if (kit.inStock) {
                                            selectedKit = kit
                                            coroutineScope.launch {
                                                sheetState.show()
                                            }
                                        }
                                    }
                                )
                            }
                            repeat(3 - rowKits.size) {
                                Spacer(modifier = Modifier.width(120.dp))
                            }
                        }
                    }
                }
            }
        }

        // FIXED: Use ModalBottomSheet instead of ModalBottomSheetLayout
        if (selectedKit != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    coroutineScope.launch { sheetState.hide() }
                    selectedKit = null
                },
                sheetState = sheetState,
                dragHandle = null
            ) {
                TShirtBottomSheet(
                    kit = selectedKit!!,
                    onAddToCart = { tshirt ->
                        onAddToCart(tshirt)
                        coroutineScope.launch { sheetState.hide() }
                        selectedKit = null
                    },
                    onDismiss = {
                        coroutineScope.launch { sheetState.hide() }
                        selectedKit = null
                    }
                )
            }
        }
    }
}

@Composable
fun TShirtBottomSheet(
    kit: Kit,
    onAddToCart: (TShirt) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedSize by remember { mutableStateOf(kit.sizeOptions.firstOrNull() ?: "M") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "${kit.kitType.replaceFirstChar { it.uppercase() }} - ${kit.season}",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        val displayImageUrl = if (!kit.imageUrl.isNullOrEmpty()) {
            kit.imageUrl
        } else {
            kit.comingSoon
        }

        if (!displayImageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = displayImageUrl,
                contentDescription = kit.kitType,
                modifier = Modifier
                    .size(150.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Select Size:", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            kit.sizeOptions.forEach { size ->
                Button(
                    onClick = { selectedSize = size },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedSize == size) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    )
                ) {
                    Text(
                        size,
                        color = if (selectedSize == size) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Price: $${"%.2f".format(kit.price)}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
            }
            Button(
                onClick = {
                    onAddToCart(
                        TShirt(
                            name = kit.kitType,
                            color = kit.season,
                            imageResourceId = R.drawable.tshirt_black,
                            size = selectedSize,
                            price = kit.price
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Add to Cart")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Keep your TShirtItem composable the same as before
@Composable
fun TShirtItem(
    baseName: String,
    colorName: String,
    imageUrl: String?,
    comingSoon: String?,
    price: Double,
    inStock: Boolean,
    onClick: () -> Unit
) {
    val displayImageUrl = if (inStock && !imageUrl.isNullOrEmpty()) {
        imageUrl
    } else if (!comingSoon.isNullOrEmpty()) {
        comingSoon
    } else {
        null
    }

    val isComingSoon = !inStock

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(120.dp)
            .clickable(enabled = inStock) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(MaterialTheme.shapes.medium)
        ) {
            if (!displayImageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = displayImageUrl,
                    contentDescription = if (isComingSoon) "Coming Soon" else "$baseName - $colorName",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Image", textAlign = TextAlign.Center)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "$baseName\n$colorName",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = if (isComingSoon) Color.Gray else LocalContentColor.current
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            "$${"%.2f".format(price)}",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isComingSoon) Color.Gray else MaterialTheme.colorScheme.primary
        )

        if (isComingSoon) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                "Coming Soon",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
        }
    }
}