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
            .imePadding()
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

        // write journal
        OutlinedTextField(
            value = state.input,
            onValueChange = vm::onInputChange,
            label = { Text("Write your journal") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 6,
            maxLines= 15,
            enabled = !state.isAnalyzing
        )

        Spacer(Modifier.height(12.dp))

        LaunchedEffect(state.isAnalyzing) {
            if (state.isAnalyzing) {
                elapsed = 0
                while (true) {
                    delay(1000)
                    elapsed++
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = vm::send,
                enabled = !state.isAnalyzing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text(if (state.isAnalyzing) "Analyzing..." else "Analyze")
                if (state.isAnalyzing) {
                    Spacer(Modifier.width(8.dp))
                    Text("${elapsed}s")
                }
            }
        }

        Spacer(Modifier.height(12.dp))

// --- Sort controls ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            var sortMenu by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = sortMenu,
                onExpandedChange = { sortMenu = it }
            ) {
                OutlinedTextField(
                    value = state.sortMethod,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    label = { Text("Sort Method") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sortMenu) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                        .weight(1f)
                )
                ExposedDropdownMenu(
                    expanded = sortMenu,
                    onDismissRequest = { sortMenu = false }
                ) {
                    listOf("Bubble", "Insertion", "Selection").forEach { m ->
                        DropdownMenuItem(text = { Text(m) }, onClick = {
                            vm.onSortMethodSelected(m); sortMenu = false
                        })
                    }
                }
            }

            Spacer(Modifier.width(8.dp))
            Button(onClick = vm::sortEntries) { Text("Sort") }
        }

        Spacer(Modifier.height(12.dp))

// --- Search controls ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            var searchMenu by remember { mutableStateOf(false) }
            var emotionMenu by remember { mutableStateOf(false) }

            // Search method dropdown
            ExposedDropdownMenuBox(
                expanded = searchMenu,
                onExpandedChange = { searchMenu = it }
            ) {
                OutlinedTextField(
                    value = state.searchMethod,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    label = { Text("Search Method") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = searchMenu) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                        .weight(1f)
                )
                ExposedDropdownMenu(
                    expanded = searchMenu,
                    onDismissRequest = { searchMenu = false }
                ) {
                    listOf("BinaryTree", "HashMap", "DoublyLinkedList").forEach { m ->
                        DropdownMenuItem(text = { Text(m) }, onClick = {
                            vm.onSearchMethodSelected(m); searchMenu = false
                        })
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            // Target emotion dropdown
            var targetEmotion by remember { mutableStateOf("JOY") }
            ExposedDropdownMenuBox(
                expanded = emotionMenu,
                onExpandedChange = { emotionMenu = it }
            ) {
                OutlinedTextField(
                    value = targetEmotion,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    label = { Text("Target Emotion") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = emotionMenu) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                        .weight(1f)
                )
                ExposedDropdownMenu(
                    expanded = emotionMenu,
                    onDismissRequest = { emotionMenu = false }
                ) {
                    listOf("JOY","SADNESS","ANGER","FEAR","DISGUST","SURPRISE","NEUTRAL").forEach { e ->
                        DropdownMenuItem(text = { Text(e) }, onClick = {
                            targetEmotion = e; emotionMenu = false
                        })
                    }
                }
            }

            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                emotionFromString(targetEmotion)?.let { vm.searchByEmotion(it) }
            }) { Text("Search") }
        }


        // message list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(state.entries) {
                entry ->
                val isHit = state.highlightedIds.contains(entry.id)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isHit)
                            MaterialTheme.colorScheme.tertiaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("📝 ${entry.text}")
                        Spacer(Modifier.height(4.dp))
                        Text("Emotion: ${entry.emotion} ${entry.emotion?.emoji() ?: ""}", fontWeight = FontWeight.Bold) // add entry emotion
                        Text("Advice: ${entry.advice}")
                    }
                }
            }
        }
    }
}
