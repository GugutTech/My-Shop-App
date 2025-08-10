package net.gugut.mypayapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import net.gugut.mypayapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentOptionsBottomSheet(
    totalAmount: String,
    onPayPalClick: () -> Unit,
    onGooglePayClick: () -> Unit,
    onCardClick: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Choose Payment Method",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Total: $totalAmount",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onPayPalClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pay with PayPal")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onGooglePayClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Pay with Google Pay", color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onCardClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pay with Card")
            }
        }
    }
}


@Composable
fun PaymentOptionButton(
    text: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = Color.White)
    }
}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PaymentOptionsBottomSheet(
//    totalAmount: String,
//    onPayPalClick: () -> Unit,
//    onDismiss: () -> Unit
//) {
//    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
//
//    ModalBottomSheet(
//        onDismissRequest = { onDismiss() },
//        sheetState = sheetState
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(20.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            Text("Choose Payment Method", style = MaterialTheme.typography.headlineSmall)
//            Text("Total: $totalAmount", style = MaterialTheme.typography.bodyLarge)
//
//            Button(
//                modifier = Modifier.fillMaxWidth(),
//                onClick = {
//                    onPayPalClick()
//                    onDismiss()
//                }
//            ) {
//                Text("Pay with PayPal")
//            }
//
//            OutlinedButton(
//                modifier = Modifier.fillMaxWidth(),
//                onClick = { onDismiss() }
//            ) {
//                Text("Cancel")
//            }
//        }
//    }
//}
