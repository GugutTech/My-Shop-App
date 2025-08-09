package net.gugut.mypayapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import net.gugut.mypayapp.viewModel.AiChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    navController: NavController,
    viewModel: AiChatViewModel = viewModel())
{
    val messages by viewModel.messages.collectAsState()
    var userInput by remember { mutableStateOf(TextFieldValue("")) }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp)
        ) {
            Text(
                "Chat With Us",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                messages.forEach { message ->
                    val isUser = message.isUser
                    val alignment = if (isUser) Alignment.End else Alignment.Start
                    val bubbleColor = if (isUser) Color(0xFFDCF8C6) else Color(0xFFE5E5EA)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .background(bubbleColor, shape = MaterialTheme.shapes.medium)
                                .padding(10.dp)
                                .widthIn(max = 280.dp)
                        ) {
                            Text(text = message.text)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            Divider(modifier = Modifier.padding(vertical = 6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type your message...") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val text = userInput.text.trim()
                        if (text.isNotEmpty()) {
                            viewModel.sendMessage(text)
                            userInput = TextFieldValue("")
                        }
                    }
                ) {
                    Text("Send")
                }
            }
        }
    }
}
