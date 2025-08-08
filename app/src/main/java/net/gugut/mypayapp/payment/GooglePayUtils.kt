package net.gugut.mypayapp.payment

import android.app.Activity
import android.content.Context
import com.google.android.gms.wallet.*
import org.json.JSONArray
import org.json.JSONObject

object GooglePayUtils {

    fun createPaymentsClient(context: Context, activity: Activity): PaymentsClient {
        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(WalletConstants.ENVIRONMENT_TEST) // Use ENVIRONMENT_PRODUCTION when live
            .build()
        return Wallet.getPaymentsClient(activity, walletOptions)
    }

    fun getPaymentDataRequest(totalPrice: String): PaymentDataRequest {
        val requestJson = JSONObject().apply {
            put("apiVersion", 2)
            put("apiVersionMinor", 0)
            put("allowedPaymentMethods", JSONArray().put(getCardPaymentMethod()))
            put("transactionInfo", getTransactionInfo(totalPrice))
            put("merchantInfo", getMerchantInfo())
        }

        return PaymentDataRequest.fromJson(requestJson.toString())
    }

    private fun getCardPaymentMethod(): JSONObject {
        val parameters = JSONObject().apply {
            put("allowedAuthMethods", JSONArray().put("PAN_ONLY").put("CRYPTOGRAM_3DS"))
            put("allowedCardNetworks", JSONArray().put("VISA").put("MASTERCARD"))
        }

        val tokenizationSpec = JSONObject().apply {
            put("type", "PAYMENT_GATEWAY")
            put("parameters", JSONObject().apply {
                put("gateway", "example") // TODO: Replace with "stripe", "braintree", etc.
                put("gatewayMerchantId", "exampleMerchantId")
            })
        }

        return JSONObject().apply {
            put("type", "CARD")
            put("parameters", parameters)
            put("tokenizationSpecification", tokenizationSpec)
        }
    }

    private fun getTransactionInfo(totalPrice: String): JSONObject {
        return JSONObject().apply {
            put("totalPrice", totalPrice)
            put("totalPriceStatus", "FINAL")
            put("currencyCode", "USD")
        }
    }

    fun getIsReadyToPayRequest(): IsReadyToPayRequest {
        val allowedAuthMethods = JSONArray().put("PAN_ONLY").put("CRYPTOGRAM_3DS")
        val allowedCardNetworks = JSONArray().put("VISA").put("MASTERCARD")

        val cardPaymentMethod = JSONObject().apply {
            put("type", "CARD")
            put("parameters", JSONObject().apply {
                put("allowedAuthMethods", allowedAuthMethods)
                put("allowedCardNetworks", allowedCardNetworks)
            })
        }

        val requestJson = JSONObject().apply {
            put("apiVersion", 2)
            put("apiVersionMinor", 0)
            put("allowedPaymentMethods", JSONArray().put(cardPaymentMethod))
        }

        return IsReadyToPayRequest.fromJson(requestJson.toString())
    }

    private fun getMerchantInfo(): JSONObject {
        return JSONObject().apply {
            put("merchantName", "My Pay App")
        }
    }
}
