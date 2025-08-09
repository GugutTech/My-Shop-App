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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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

@Composable
fun TShirtDetailBottomSheet(
    baseName: String,
    colorToImageRes: Map<String, Int>,  // Map color -> image resource
    colors: List<String>,
    sizes: List<String>,
    sizePrices: Map<String, Double>,
    colorPrices: Map<String, Double>,
    onAddToCart: (TShirt) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedSize by remember { mutableStateOf(sizes.firstOrNull() ?: "M") }
    var selectedColor by remember { mutableStateOf(colors.firstOrNull() ?: "Black") }

    val sizePrice = sizePrices[selectedSize] ?: 0.0
    val colorPrice = colorPrices[selectedColor] ?: 0.0
    val unroundedTotal = sizePrice + colorPrice
    val totalPrice = roundPrice(unroundedTotal)

    val imageRes = colorToImageRes[selectedColor] ?: colorToImageRes.values.first()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
    ) {
        Text(
            text = baseName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(16.dp))

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "T-Shirt - $selectedColor",
            modifier = Modifier
                .height(280.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Fit
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Price: $${"%.2f".format(totalPrice)}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(24.dp))

        Text("Select Size", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { // smaller spacing
            sizes.forEach { size ->
                OutlinedButton(
                    onClick = { selectedSize = size },
                    modifier = Modifier.size(36.dp, 36.dp), // smaller size buttons
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedSize == size)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else Color.Transparent
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (selectedSize == size)
                            MaterialTheme.colorScheme.primary
                        else Color.Gray
                    )
                ) {
                    Text(
                        text = size,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Text("Select Color", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {  // smaller spacing
            colors.forEach { colorName ->
                val colorValue = when (colorName.lowercase()) {
                    "black" -> Color.Black
                    "blue" -> Color.Blue
                    "white" -> Color.White
                    "green" -> Color.Green
                    else -> Color.Gray
                }
                Box(
                    modifier = Modifier
                        .size(24.dp) // smaller circle
                        .clip(CircleShape)
                        .background(colorValue)
                        .border(
                            width = if (selectedColor == colorName) 3.dp else 1.dp,
                            color = if (selectedColor == colorName) Color.Black else Color.Gray,
                            shape = CircleShape
                        )
                        .clickable { selectedColor = colorName }
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total: $${"%.2f".format(totalPrice)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Button(onClick = {
                val newShirt = TShirt(
                    name = "$baseName - $selectedSize - $selectedColor",
                    imageResourceId = imageRes,
                    size = selectedSize,
                    color = selectedColor,
                    price = totalPrice
                )
                onAddToCart(newShirt)
            }) {
                Text("Add to Cart")
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

//@Composable
//fun TShirtDetailBottomSheet(
//    baseName: String,
//    colorToImageRes: Map<String, Int>,  // Map color -> image resource
//    colors: List<String>,
//    sizes: List<String>,
//    sizePrices: Map<String, Double>,
//    colorPrices: Map<String, Double>,
//    onAddToCart: (TShirt) -> Unit,
//    onDismiss: () -> Unit
//) {
//    var selectedSize by remember { mutableStateOf(sizes.firstOrNull() ?: "M") }
//    var selectedColor by remember { mutableStateOf(colors.firstOrNull() ?: "Black") }
//
//    val sizePrice = sizePrices[selectedSize] ?: 0.0
//    val colorPrice = colorPrices[selectedColor] ?: 0.0
//    val unroundedTotal = sizePrice + colorPrice
//    val totalPrice = roundPrice(unroundedTotal)
//
//    // Get image resource for the currently selected color, fallback to first color image
//    val imageRes = colorToImageRes[selectedColor] ?: colorToImageRes.values.first()
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(24.dp)
//            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
//    ) {
//        Text(
//            text = baseName,
//            fontSize = 20.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.align(Alignment.CenterHorizontally)
//        )
//
//        Spacer(Modifier.height(16.dp))
//
//        Image(
//            painter = painterResource(id = imageRes),
//            contentDescription = "T-Shirt - $selectedColor",
//            modifier = Modifier
//                .height(200.dp)
//                .fillMaxWidth()
//                .clip(RoundedCornerShape(12.dp))
//                .align(Alignment.CenterHorizontally),
//            contentScale = ContentScale.Crop
//        )
//
//        Spacer(Modifier.height(24.dp))
//
//        Text("Select Size", fontWeight = FontWeight.SemiBold)
//        Spacer(Modifier.height(8.dp))
//        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
//            sizes.forEach { size ->
//                OutlinedButton(
//                    onClick = { selectedSize = size },
//                    colors = ButtonDefaults.outlinedButtonColors(
//                        containerColor = if (selectedSize == size)
//                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
//                        else Color.Transparent
//                    ),
//                    border = BorderStroke(
//                        1.dp,
//                        if (selectedSize == size)
//                            MaterialTheme.colorScheme.primary
//                        else Color.Gray
//                    )
//                ) {
//                    Text(size)
//                }
//            }
//        }
//
//        Spacer(Modifier.height(24.dp))
//
//        Text("Select Color", fontWeight = FontWeight.SemiBold)
//        Spacer(Modifier.height(8.dp))
//        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
//            colors.forEach { colorName ->
//                val colorValue = when (colorName.lowercase()) {
//                    "black" -> Color.Black
//                    "blue" -> Color.Blue
//                    "white" -> Color.White
//                    "green" -> Color.Green
//                    else -> Color.Gray
//                }
//                Box(
//                    modifier = Modifier
//                        .size(32.dp)
//                        .clip(CircleShape)
//                        .background(colorValue)
//                        .border(
//                            width = if (selectedColor == colorName) 3.dp else 1.dp,
//                            color = if (selectedColor == colorName) Color.Black else Color.Gray,
//                            shape = CircleShape
//                        )
//                        .clickable { selectedColor = colorName }
//                )
//            }
//        }
//
//        Spacer(Modifier.height(32.dp))
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            Button(onClick = {
//                val newShirt = TShirt(
//                    name = "$baseName - $selectedSize - $selectedColor",
//                    imageResourceId = imageRes,
//                    size = selectedSize,
//                    color = selectedColor,
//                    price = totalPrice
//                )
//                onAddToCart(newShirt)
//            }) {
//                Text("Add to Cart")
//            }
//        }
//
//        Spacer(Modifier.height(16.dp))
//    }
//}