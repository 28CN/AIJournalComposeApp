package com.example.ollamaserverapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.ollamaserverapp.presentation.ChatViewModel
import com.example.ollamaserverapp.ui.screen.*

@Composable
fun AppNav(vm: ChatViewModel, navController: NavHostController) {
    val navItems = listOf(
        NavItem("Write", "write"),
        NavItem("History", "history"),
        NavItem("Chart", "chart"),
        NavItem("Help", "help")
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                navItems.forEach { item ->
                    NavigationBarItem(
                        selected = navController.currentBackStackEntryAsState().value?.destination?.route == item.route,
                        onClick = { navController.navigate(item.route) },
                        icon = {}, // 没用图标，文字导航
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "write",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("write") {
                WriteScreen(vm)
            }
            composable("history") {
                HistoryScreen(vm)
            }
            composable("chart") {
                ChartScreen(vm)
            }
            composable("help") {
                HelpScreen()
            }
        }
    }
}

data class NavItem(val label: String, val route: String)
