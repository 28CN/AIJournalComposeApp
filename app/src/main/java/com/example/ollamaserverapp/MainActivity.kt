package com.example.ollamaserverapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.ollamaserverapp.presentation.ChatViewModel
import com.example.ollamaserverapp.ui.navigation.AppNav
import com.example.ollamaserverapp.ui.theme.AppPalette
import com.example.ollamaserverapp.ui.theme.OllamaServerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OllamaServerAppTheme(
                palette = AppPalette.ElegantIvoryBlue
            ) {
                val navController = rememberNavController()
                val vm: ChatViewModel = viewModel()

                AppNav(vm = vm, navController = navController)
            }
        }
    }
}
