package net.gugut.mypayapp.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import net.gugut.mypayapp.api.PayPalApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.foundation.layout.*

@Composable
fun PayPalCheckoutScreen(
    clientID: String = "AfQc_3M9KGo9lRgzu_qqbUoEC0GGb11H-FW5WU0GS9EXJWVGOz9Ltx4tDdmb7uY5y3v5qryCsnwAHze5",
    secretID: String = "EPJyn1ZS2yOZEInlhO1bb5qnl_NkBfU4q_di8O8GQpFCEBOAH99xZBJloPincgXFbpMBz6kr383A9LVw",
    returnUrl: String = "mypayapp://paypal-return",
    amount: String = "5.00",
    currencyCode: String = "USD",
    onPaymentSuccess: () -> Unit,
    onPaymentCanceled: () -> Unit,
    onPaymentError: (String) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var accessToken by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) } // start loading immediately
    var orderId by remember { mutableStateOf<String?>(null) }

    // Retrofit setup...
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("https://sandbox.paypal.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val payPalApi = remember { retrofit.create(PayPalApi::class.java) }

    // Get access token on enter
    LaunchedEffect(clientID, secretID) {
        val authString = "$clientID:$secretID"
        val encodedAuthString = android.util.Base64.encodeToString(authString.toByteArray(), android.util.Base64.NO_WRAP)
        try {
            val response = payPalApi.getAccessToken("Basic $encodedAuthString")
            if (response.isSuccessful) {
                accessToken = response.body()?.access_token
            } else {
                onPaymentError("Failed to fetch access token: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            onPaymentError("Network error: ${e.localizedMessage}")
        }
        isLoading = false
    }

    // Automatically start order once accessToken is ready
    LaunchedEffect(accessToken) {
        if (accessToken != null) {
            isLoading = true
            val uniqueId = java.util.UUID.randomUUID().toString()
            val orderRequestJson = buildOrderRequestJson(uniqueId, amount, currencyCode, returnUrl)
            try {
                val response = payPalApi.createOrder(
                    authorization = "Bearer $accessToken",
                    requestId = uniqueId,
                    orderRequest = orderRequestJson
                )
                if (response.isSuccessful) {
                    orderId = response.body()?.id
                    if (orderId != null && context is androidx.fragment.app.FragmentActivity) {
                        val config = com.paypal.android.corepayments.CoreConfig(clientID, environment = com.paypal.android.corepayments.Environment.SANDBOX)
                        val payPalWebCheckoutClient = com.paypal.android.paypalwebpayments.PayPalWebCheckoutClient(context, config, returnUrl)
                        payPalWebCheckoutClient.listener = object : com.paypal.android.paypalwebpayments.PayPalWebCheckoutListener {
                            override fun onPayPalWebSuccess(result: com.paypal.android.paypalwebpayments.PayPalWebCheckoutResult) {
                                onPaymentSuccess()
                            }

                            override fun onPayPalWebFailure(error: com.paypal.android.corepayments.PayPalSDKError) {
                                onPaymentError(error.message ?: "PayPal payment failed")
                            }

                            override fun onPayPalWebCanceled() {
                                onPaymentCanceled()
                            }
                        }
                        val request = com.paypal.android.paypalwebpayments.PayPalWebCheckoutRequest(orderId!!, fundingSource = com.paypal.android.paypalwebpayments.PayPalWebCheckoutFundingSource.PAYPAL)
                        payPalWebCheckoutClient.start(request)
                    }
                } else {
                    onPaymentError("Order creation failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onPaymentError("Network error: ${e.localizedMessage}")
            }
            isLoading = false
        }
    }

    // UI
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Starting PayPal Authentication...")
            }
        } else {
            Text("Waiting for PayPal interaction...")
        }
    }
}

// Helper to build order request data class or JSON compatible with Retrofit/Gson
fun buildOrderRequestJson(
    uniqueId: String,
    amount: String,
    currencyCode: String,
    returnUrl: String
): OrderRequest {
    // Build the OrderRequest data class with your fields, or use a Map<String, Any> if you want more flexibility
    // For example:

    return OrderRequest(
        intent = "CAPTURE",
        purchase_units = listOf(
            PurchaseUnit(
                reference_id = uniqueId,
                amount = Amount(currency_code = currencyCode, value = amount)
            )
        ),
        payment_source = PaymentSource(
            paypal = PayPalExperienceContext(
                experience_context = ExperienceContext(
                    payment_method_preference = "IMMEDIATE_PAYMENT_REQUIRED",
                    brand_name = "Your Brand",
                    locale = "en-US",
                    landing_page = "LOGIN",
                    shipping_preference = "NO_SHIPPING",
                    user_action = "PAY_NOW",
                    return_url = returnUrl,
                    cancel_url = "https://example.com/cancelUrl"
                )
            )
        )
    )
}

// Define data classes for the order request matching PayPal API structure
data class OrderRequest(
    val intent: String,
    val purchase_units: List<PurchaseUnit>,
    val payment_source: PaymentSource
)

data class PurchaseUnit(
    val reference_id: String,
    val amount: Amount
)

data class Amount(
    val currency_code: String,
    val value: String
)

data class PaymentSource(
    val paypal: PayPalExperienceContext
)

data class PayPalExperienceContext(
    val experience_context: ExperienceContext
)

data class ExperienceContext(
    val payment_method_preference: String,
    val brand_name: String,
    val locale: String,
    val landing_page: String,
    val shipping_preference: String,
    val user_action: String,
    val return_url: String,
    val cancel_url: String
)
