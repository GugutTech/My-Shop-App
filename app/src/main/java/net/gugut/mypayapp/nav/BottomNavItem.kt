package net.gugut.mypayapp.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Cart : BottomNavItem("cart", "Cart", Icons.Default.ShoppingCart)
    object Chat : BottomNavItem("chat", "Chat", Icons.Default.Chat)
}
