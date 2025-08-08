package net.gugut.mypayapp.util

import android.util.Log
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import net.gugut.mypayapp.api.GeoapifyService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun formatCardNumber(input: String): String {
    val digits = input.filter { it.isDigit() }.take(16)
    return digits.chunked(4).joinToString(" ")
}

fun formatExpiryDate(input: String): String {
    val digits = input.filter { it.isDigit() }.take(4)
    return when {
        digits.length >= 3 -> "${digits.take(2)}/${digits.drop(2)}"
        else -> digits
    }
}

fun formatCVV(input: String, cardNumber: String): String {
    val digits = input.filter { it.isDigit() }
    return if (isAmex(cardNumber)) digits.take(4) else digits.take(3)
}

fun isAmex(cardNumber: String): Boolean {
    return cardNumber.replace(" ", "").startsWith("34") || cardNumber.replace(" ", "").startsWith("37")
}

suspend fun fetchCityStateFromZip(zip: String, onResult: (String, String) -> Unit) {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.geoapify.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(GeoapifyService::class.java)

    try {
        val response = service.getCityStateFromZip(zip, "a078a0a9cda64d9cbe5b810ce45b4ec8")
        val props = response.features.firstOrNull()?.properties
        if (props?.city != null && props.state != null) {
            onResult(props.city, props.state)
        }
    } catch (e: Exception) {
        Log.e("Geoapify", "Error fetching city/state", e)
    }
}
fun formatPhoneNumber(input: String): String {
    val digits = input.filter { it.isDigit() }

    return when {
        digits.length <= 3 -> "(${digits}"
        digits.length <= 6 -> "(${digits.substring(0,3)}) ${digits.substring(3)}"
        digits.length <= 10 -> "(${digits.substring(0,3)}) ${digits.substring(3,6)}-${digits.substring(6)}"
        else -> "(${digits.substring(0,3)}) ${digits.substring(3,6)}-${digits.substring(6,10)}"
    }
}

enum class CardType {
    VISA, MASTERCARD, AMEX, UNKNOWN
}

fun getCardType(cardNumber: String): CardType {
    val cleaned = cardNumber.replace(" ", "")
    return when {
        cleaned.startsWith("4") -> CardType.VISA
        cleaned.matches(Regex("^5[1-5].*")) || cleaned.matches(Regex("^2(2[2-9]|[3-7][0-9]).*")) -> CardType.MASTERCARD
        cleaned.startsWith("34") || cleaned.startsWith("37") -> CardType.AMEX
        else -> CardType.UNKNOWN
    }
}

fun isValidCardNumber(cardNumber: String): Boolean {
    val cleaned = cardNumber.replace(" ", "")
    if (cleaned.length !in 13..19) return false
    var sum = 0
    var alternate = false
    for (i in cleaned.length - 1 downTo 0) {
        var n = cleaned[i].digitToInt()
        if (alternate) {
            n *= 2
            if (n > 9) n -= 9
        }
        sum += n
        alternate = !alternate
    }
    return (sum % 10 == 0)
}

fun isExpiryValid(expiry: String): Boolean {
    val parts = expiry.split("/")
    if (parts.size != 2) return false
    val month = parts[0].toIntOrNull() ?: return false
    val year = parts[1].toIntOrNull() ?: return false
    if (month !in 1..12) return false

    val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100
    val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1

    return year > currentYear || (year == currentYear && month >= currentMonth)
}

fun isCVVValid(cvv: String, cardType: CardType): Boolean {
    return when (cardType) {
        CardType.AMEX -> cvv.length == 4
        CardType.VISA, CardType.MASTERCARD -> cvv.length == 3
        else -> false
    }
}

fun formatInputWithCursor(
    oldValue: TextFieldValue,
    newText: String,
    formatter: (String) -> String
): TextFieldValue {
    val formattedText = formatter(newText.filter { it.isDigit() })

    // Place cursor always at the end
    val cursorPosition = formattedText.length

    return TextFieldValue(
        text = formattedText,
        selection = TextRange(cursorPosition)
    )
}

