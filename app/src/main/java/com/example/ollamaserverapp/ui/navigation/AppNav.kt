package com.example.ollamaserverapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ollamaserverapp.presentation.ChatViewModel
import com.example.ollamaserverapp.ui.screen.ChatScreen


object Route {
    const val CHAT = "chat"

    // const val Settings = "settings" // 以后想扩，就加这里
}

@Composable
fun AppNavHost(nav: NavHostController) {
    val chatVm: ChatViewModel = viewModel()

    NavHost(navController = nav, startDestination = Route.CHAT) {
        composable(Route.CHAT) {
            LaunchedEffect(Unit) {
                chatVm.loadModels()   // load model list while opening
            }
            ChatScreen(chatVm)
        }
        // composable(Route.Settings) { SettingsScreen(...) }
    }
}
