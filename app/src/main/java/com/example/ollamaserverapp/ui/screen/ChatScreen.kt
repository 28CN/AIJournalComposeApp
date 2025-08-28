@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.ollamaserverapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ollamaserverapp.presentation.ChatViewModel
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.Color
import com.example.ollamaserverapp.model.*


@Composable
fun ChatScreen(vm: ChatViewModel) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // dropdown box to choose model.
        var elapsed by remember { mutableIntStateOf(0) }
        var expanded by remember { mutableStateOf(false) }

        LaunchedEffect(state.models) { if (state.models.isNotEmpty()) expanded = false }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            // text field
            OutlinedTextField(
                value = state.selectedModel ?: if (state.models.isEmpty()) "Loading..." else "Select model",
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = { Text("Model") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)     // 关键：设置锚点
                    .fillMaxWidth(),
                enabled = state.models.isNotEmpty()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                state.models.forEach { model ->
                    DropdownMenuItem(
                        text = { Text(model) },
                        onClick = {
                            vm.onModelSelected(model)
                            expanded = false
                        }
                    )
                }
            }
        }

        if (state.models.isNotEmpty()) {
            Text(
                text = "Note: First load or switching model may cause the next response to be slow.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(12.dp))

        // text box
        OutlinedTextField(
            value = state.input,
            onValueChange = vm::onInputChange,
            label = { Text("Ask anything") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSending
        )

        Spacer(Modifier.height(12.dp))

        // send button
        LaunchedEffect(state.isSending, elapsed) {
            if (state.isSending) {
                delay(1000)
                elapsed++
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = vm::send,
                enabled = !state.isSending,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text(if (state.isSending) "Sending..." else "Send to Ollama")
            }
            if (state.isSending) {
                Spacer(Modifier.width(8.dp))
                Text("${elapsed}s")
            }
        }

        Spacer(Modifier.height(12.dp))

        // message list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(state.messages) { message ->
                Row {
                    Text(
                        text = if (message.role == Role.You) "You:" else "Bot:",
                        fontWeight = FontWeight.Bold,
                        color = if (message.role == Role.You)
                            MaterialTheme.colorScheme.secondary
                        else
                            MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(message.content)
                }
            }
        }
    }
}
