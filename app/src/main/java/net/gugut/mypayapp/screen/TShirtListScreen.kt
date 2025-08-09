package net.gugut.mypayapp.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.rememberModalBottomSheetState

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.gugut.mypayapp.R
import net.gugut.mypayapp.model.TShirt

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

fun roundPrice(value: Double): Double {
    return (value * 100).roundToInt() / 100.0
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TShirtListScreen(
    onAddToCart: (TShirt) -> Unit,
    onViewCart: () -> Unit,
    navController: NavController
) {
    val baseName = "Classic Tee"
    val colors = listOf("Black", "Blue", "White", "Green")
    val colorToImageRes = mapOf(
        "Black" to R.drawable.tshirt_black,
        "Blue" to R.drawable.tshirt_blue,
        "White" to R.drawable.tshirt_white,
        "Green" to R.drawable.tshirt_red
    )

    val sizes = listOf("S", "M", "L", "XL")
    val sizePrices = mapOf("S" to 14.99, "M" to 15.99, "L" to 16.99, "XL" to 17.99)
    val colorPrices = mapOf("Black" to 0.0, "Blue" to 0.5, "White" to 0.25, "Green" to 0.75)

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTShirt by remember { mutableStateOf<TShirt?>(null) }

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
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(7) { rowIndex ->
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(5) { colIndex ->
                            val colorIndex = (rowIndex + colIndex) % colors.size
                            val colorName = colors[colorIndex]
                            val imageRes = colorToImageRes[colorName] ?: R.drawable.tshirt_black
                            val baseSize = sizes.first()
                            val basePrice = sizePrices[baseSize] ?: 15.0
                            val colorExtra = colorPrices[colorName] ?: 0.0
                            val unroundedTotal = basePrice + colorExtra
                            val totalPrice = roundPrice(unroundedTotal)

                            TShirtItem(
                                baseName = baseName,
                                colorName = colorName,
                                imageRes = imageRes,
                                price = totalPrice, // pass price here
                                onClick = {
                                    selectedTShirt = TShirt(
                                        name = baseName,
                                        color = colorName,
                                        imageResourceId = imageRes,
                                        size = baseSize,
                                        price = totalPrice
                                    )
                                    coroutineScope.launch { sheetState.show() }
                                }
                            )
                        }
                    }
                }
            }

            if (selectedTShirt != null) {
                ModalBottomSheet(
                    onDismissRequest = {
                        coroutineScope.launch { sheetState.hide() }
                        selectedTShirt = null
                    },
                    sheetState = sheetState,
                    dragHandle = null
                ) {
                    TShirtDetailBottomSheet(
                        baseName = selectedTShirt!!.name,
                        colorToImageRes = colorToImageRes,
                        colors = colors,
                        sizes = sizes,
                        sizePrices = sizePrices,
                        colorPrices = colorPrices,
                        onAddToCart = {
                            onAddToCart(it)
                            coroutineScope.launch { sheetState.hide() }
                            selectedTShirt = null
                        },
                        onDismiss = {
                            coroutineScope.launch { sheetState.hide() }
                            selectedTShirt = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TShirtItem(
    baseName: String,
    colorName: String,
    imageRes: Int,
    price: Double,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "$baseName - $colorName",
            modifier = Modifier
                .size(100.dp)
                .clip(MaterialTheme.shapes.medium)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$baseName\n$colorName",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "$${"%.2f".format(price)}",  // Display price with 2 decimals
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}