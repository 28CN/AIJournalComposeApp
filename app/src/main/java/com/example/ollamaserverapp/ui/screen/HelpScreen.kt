package com.example.ollamaserverapp.ui.screen

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp

@Composable
fun HelpScreen() {
    // Loads assets/help.html
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = false
                loadUrl("file:///android_asset/help.html")
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    )
}
