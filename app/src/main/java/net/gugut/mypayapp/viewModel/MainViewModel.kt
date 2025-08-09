package net.gugut.mypayapp.viewModel

import  android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.gugut.mypayapp.model.TShirt
import net.gugut.mypayapp.model.User
import net.gugut.mypayapp.notification.NotificationHelper
import net.gugut.mypayapp.util.*  // your utils (formatting, fetchCityStateFromZip, etc)

class MainViewModel(
    application: Application
) : AndroidViewModel(application) {

    var firstNameTouched by mutableStateOf(false)
    var lastNameTouched by mutableStateOf(false)
    var phoneTouched by mutableStateOf(false)
    var addressTouched by mutableStateOf(false)
    var zipCodeTouched by mutableStateOf(false)

    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var loginError by mutableStateOf<String?>(null)

    private fun checkIfUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    // --- Cart State and Operations ---
    private val _cartItems = MutableStateFlow<Map<TShirt, Int>>(emptyMap())
    val cartItems: StateFlow<Map<TShirt, Int>> = _cartItems

    private val sharedPreferences =
        application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _user = MutableStateFlow<User?>(null)  // Ensure you have this
    val currentUser = _user.asStateFlow()

    init {
        loadUserFromStorage()  // Load user data on ViewModel start
    }

    fun updateUser(email: String, username: String): Boolean {
        if (email.isBlank() || username.isBlank()) return false

        val updated = _user.value?.copy(email = email, username = username)
        _user.value = updated
        saveUserToStorage(updated)
        return true
    }

    fun changePassword(current: String, new: String): Boolean {
        val storedPassword = sharedPreferences.getString("savedPassword", "") ?: return false

        if (current != storedPassword || new.isBlank()) return false

        sharedPreferences.edit().putString("savedPassword", new).apply()
        val updated = _user.value?.copy(password = new)
        _user.value = updated
        saveUserToStorage(updated)
        return true
    }

    var isUserLoggedIn by mutableStateOf(checkIfUserLoggedIn())
        private set

    fun registerUser(newUsername: String, newPassword: String): Boolean {
        if (newUsername.isBlank() || newPassword.isBlank()) return false
        sharedPreferences.edit()
            .putString("savedUsername", newUsername)
            .putString("savedPassword", newPassword)
            .apply()
        return true
    }


    fun loginUser(inputUsername: String, inputPassword: String): Boolean {
        val savedUsername = sharedPreferences.getString("savedUsername", "")
        val savedPassword = sharedPreferences.getString("savedPassword", "")
        return if (inputUsername == savedUsername && inputPassword == savedPassword) {
            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
            isUserLoggedIn = true
            true
        } else {
            loginError = "Invalid credentials"
            false
        }
    }

    fun logoutUser() {
        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
        isUserLoggedIn = false
    }

    fun addToCart(item: TShirt) {
        val current = _cartItems.value.toMutableMap()
        val currentQty = current[item] ?: 0
        current[item] = currentQty + 1
        _cartItems.value = current

    }

    fun removeFromCart(item: TShirt) {
        val currentList = _cartItems.value.toMutableMap()
        val currentQty = currentList[item] ?: return
        if (currentQty <= 1) {
            currentList.remove(item)
        } else {
            currentList[item] = currentQty - 1
        }
        _cartItems.value = currentList
    }

    fun getTotalPrice(): Double {
        return _cartItems.value.entries.sumOf { (item, qty) -> item.price * qty }
    }

    // --- Card Payment Form State and Validation ---
    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var phone by mutableStateOf("")
    var address by mutableStateOf("")
    var zipCode by mutableStateOf("")
    var city by mutableStateOf("")
    var state by mutableStateOf("")

    var cardNumber by mutableStateOf("")
    var expiryDate by mutableStateOf("")
    var cvv by mutableStateOf("")

    var phoneError by mutableStateOf(false)
    var cardNumberError by mutableStateOf(false)
    var expiryDateError by mutableStateOf(false)
    var cvvError by mutableStateOf(false)

    var cardType by mutableStateOf(CardType.UNKNOWN)

    fun onZipCodeChanged(newZip: String) {
        zipCode = newZip
        if (newZip.length == 5) {
            viewModelScope.launch {
                fetchCityStateFromZip(newZip) { fetchedCity, fetchedState ->
                    city = fetchedCity
                    state = fetchedState
                }
            }
        }
    }

    val shippingAddress: String
        get() = listOf(address, city, state, zipCode)
            .filter { it.isNotBlank() }
            .joinToString(", ")

    fun onPhoneChanged(newPhone: String) {
        phone = formatPhoneNumber(newPhone)
        phoneError = phone.filter { it.isDigit() }.length != 10
    }

    fun onCardNumberChanged(input: String) {
        cardNumber = formatCardNumber(input)
        cardType = getCardType(cardNumber)
        cardNumberError = !isValidCardNumber(cardNumber)
        cvvError = !isCVVValid(cvv, cardType)  // Re-validate CVV on card change
    }

    fun onExpiryDateChanged(input: String) {
        expiryDate = formatExpiryDate(input)
        expiryDateError = !isExpiryValid(expiryDate)
    }

    fun onCVVChanged(input: String) {
        cvv = formatCVV(input, cardNumber)
        cvvError = !isCVVValid(cvv, cardType)
    }

    private fun saveUserToStorage(user: User?) {
        user?.let {
            sharedPreferences.edit()
                .putString("user_email", it.email)
                .putString("user_username", it.username)
                .putString("user_password", it.password)
                .apply()
        }
    }

    private fun loadUserFromStorage() {
        val email = sharedPreferences.getString("user_email", null)
        val username = sharedPreferences.getString("user_username", null)
        val password = sharedPreferences.getString("user_password", null)

        if (!email.isNullOrBlank() && !username.isNullOrBlank() && !password.isNullOrBlank()) {
            _user.value = User(email, username, password)
        }
    }


    // --- Payment Status State ---
    private val _paymentSuccess = MutableStateFlow(false)
    val paymentSuccess: StateFlow<Boolean> = _paymentSuccess

    private val _paymentError = MutableStateFlow<String?>(null)
    val paymentError: StateFlow<String?> = _paymentError

    fun handlePaymentSuccess() {
        _paymentSuccess.value = true
        _paymentError.value = null
    }

    fun handlePaymentFailure(errorMessage: String) {
        _paymentError.value = errorMessage
        _paymentSuccess.value = false
    }

    fun resetPaymentState() {
        _paymentSuccess.value = false
        _paymentError.value = null
    }

    fun onCardNumberChanged(old: TextFieldValue, new: String): TextFieldValue {
        val result = formatInputWithCursor(old, new, ::formatCardNumber)
        cardNumber = result.text
        cardType = getCardType(cardNumber)
        cardNumberError = !isValidCardNumber(cardNumber)
        return result
    }

    fun onExpiryDateChanged(old: TextFieldValue, new: String): TextFieldValue {
        val result = formatInputWithCursor(old, new, ::formatExpiryDate)
        expiryDate = result.text
        expiryDateError = !isExpiryValid(expiryDate)
        return result
    }

    fun onPhoneChanged(old: TextFieldValue, new: String): TextFieldValue {
        val result = formatInputWithCursor(old, new, ::formatPhoneNumber)
        phone = result.text
        phoneError = phone.filter { it.isDigit() }.length != 10
        return result
    }

    fun onCVVChanged(old: TextFieldValue, new: String): TextFieldValue {
        val result = formatInputWithCursor(old, new) { formatCVV(it, cardNumber) }
        cvv = result.text
        cvvError = !isCVVValid(cvv, cardType)
        return result
    }

    fun isFormValid(): Boolean {
        return firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                phone.filter { it.isDigit() }.length == 10 &&
                address.isNotBlank() &&
                zipCode.length == 5 &&
                !cardNumberError &&
                !expiryDateError &&
                !cvvError
    }

    private val _confirmationCartItems = mutableStateOf<Map<TShirt, Int>>(emptyMap())
    val confirmationCartItems: State<Map<TShirt, Int>> = _confirmationCartItems

    private val _confirmationTotalPrice = mutableStateOf(0.0)
    val confirmationTotalPrice: State<Double> = _confirmationTotalPrice

    private val _confirmationNumber = mutableStateOf("")
    val confirmationNumber: State<String> = _confirmationNumber

    fun setConfirmationDetails(cart: Map<TShirt, Int>, total: Double) {
        _confirmationCartItems.value = cart
        _confirmationTotalPrice.value = total
        _confirmationNumber.value = generateConfirmationNumber()
    }

    fun startPayment(
        cartItems: Map<TShirt, Int>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            delay(2000) // simulate payment processing

            val total = cartItems.entries.sumOf { (item, qty) -> item.price * qty }
            setConfirmationDetails(cartItems, total)

            // ✅ Use application context
            val context = getApplication<Application>().applicationContext
            notifyOrderStatus(context, "confirmed")

            onSuccess()
        }
    }


    fun processPayment() {
        Log.d("Payment", "Processing with card: $cardNumber, exp: $expiryDate, cvv: $cvv")
        handlePaymentSuccess()
    }

    fun notifyOrderStatus(context: Context, status: String) {
        val (title, message) = when (status) {
            "confirmed" -> "Order Confirmed" to "Your order has been confirmed!"
            "shipped" -> "Order Shipped" to "Your order is on its way!"
            "delivered" -> "Order Delivered" to "Your order has been delivered!"
            else -> return
        }
        NotificationHelper.showNotification(context, title, message, status.hashCode())
    }

    // Inside MainViewModel
    private val _savedItems = MutableStateFlow<Map<TShirt, Int>>(emptyMap())
    val savedItems: StateFlow<Map<TShirt, Int>> = _savedItems

    fun saveForLater(item: TShirt) {
        val current = _savedItems.value.toMutableMap()
        val qty = cartItems.value[item] ?: 1
        current[item] = qty
        _savedItems.value = current
        removeFromCart(item)
    }

    fun moveToCartFromSaved(item: TShirt) {
        val current = _savedItems.value.toMutableMap()
        val qty = current[item] ?: 1
        current.remove(item)
        _savedItems.value = current
        addToCart(item)
    }

    fun removeSavedItem(item: TShirt) {
        val current = _savedItems.value.toMutableMap()
        current.remove(item)
        _savedItems.value = current
    }


    companion object {
        fun generateConfirmationNumber(): String {
            val chars = ('A'..'Z') + ('0'..'9')
            return (1..10).map { chars.random() }.joinToString("")
        }
    }
}
