package net.gugut.mypayapp.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val isUser: Boolean)

class AiChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    fun sendMessage(userText: String) {
        _messages.update { it + ChatMessage(userText, isUser = true) }

        CoroutineScope(Dispatchers.IO).launch {
            // Simulated AI response
            val response = getBotResponse(userText)
            _messages.update { it + ChatMessage(response, isUser = false) }
        }
    }

    private fun getBotResponse(input: String): String {
        return when {
            input.contains("hello", true) -> "Hi there! How can I help you today?"
            input.contains("order", true) -> "Can you provide your order ID?"
            input.contains("refund", true) -> "I’ll help you with your refund. What’s the reason?"
            else -> "I'm not sure how to respond to that yet, but a support agent will be notified."
        }
    }
}
