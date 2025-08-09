package net.gugut.mypayapp.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.gugut.mypayapp.model.TShirt
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import coil.compose.rememberAsyncImagePainter
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import java.util.concurrent.TimeUnit

@SuppressLint("RememberReturnType")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ConfirmationScreen(
    navController: NavController,
    cartItems: Map<TShirt, Int>,
    totalPrice: Double,
    confirmationNumber: String,
    shippingAddress: String
) {
    val expectedDeliveryDate = remember {
        LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    }

    val topEdgeParties = remember {
        val colors = listOf(
            Color.Red.toArgb(),
            Color.Green.toArgb(),
            Color.Blue.toArgb(),
            Color.Yellow.toArgb(),
            Color.Magenta.toArgb(),
            Color.Cyan.toArgb(),
            Color(0xFFFFA500).toArgb(), // Orange
            Color(0xFF8A2BE2).toArgb(), // BlueViolet
            Color(0xFF00FF7F).toArgb()  // SpringGreen
        )

        val positions = listOf(0.0, 0.25, 0.5, 0.75, 1.0) // Left to right on top edge

        positions.map { x ->
            Party(
                emitter = Emitter(duration = 1, TimeUnit.SECONDS).max(300),
                spread = 360,
                speed = 20f,
                damping = 0.9f,
                colors = colors,
                shapes = listOf(Shape.Square, Shape.Circle),
                position = Position.Relative(x, 0.0) // x along the top
            )
        }
    }

    KonfettiView(
        modifier = Modifier.fillMaxSize(),
        parties = topEdgeParties
    )


    // 🎊 Main Confirmation Content
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(32.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text("🎉 Payment Successful!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Thank you for your purchase.", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Confirmation #: ${confirmationNumber.toString()}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Expected Delivery: $expectedDeliveryDate", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(24.dp))

            Text("Shipping Address:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(shippingAddress, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(24.dp))

            Text("Items Purchased:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            cartItems.forEach { (tShirt, quantity) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(tShirt.imageResourceId),
                        contentDescription = tShirt.name,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("${tShirt.name} x $quantity", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text("Size: ${tShirt.size}, Color: ${tShirt.color}", fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Total: $${"%.2f".format(totalPrice)}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    navController.popBackStack("home", inclusive = false)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Back to Home")
            }
        }
}

