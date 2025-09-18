package com.example.ollamaserverapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ollamaserverapp.presentation.ChatViewModel
import com.example.ollamaserverapp.model.emoji
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.isActive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteScreen(vm: ChatViewModel) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    var elapsed by remember { mutableIntStateOf(0) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.loadModels()
    }

    LaunchedEffect(state.isAnalyzing) {
        if (state.isAnalyzing) {
            elapsed = 0
            while (isActive && state.isAnalyzing) {
                delay(1000)
                elapsed++
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        //  Model selector
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = state.selectedModel ?: if (state.models.isEmpty()) "Loading..." else "Select model",
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = { Text("Model") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
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

        // Journal input
        OutlinedTextField(
            value = state.input,
            onValueChange = vm::onInputChange,
            label = { Text("Write your journal") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 160.dp),
            enabled = !state.isAnalyzing
        )

        // Analyze button
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

        //  Result entries
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(state.entries) { entry ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("📝 ${entry.text}")
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Emotion: ${entry.emotion} ${entry.emotion?.emoji() ?: ""}",
                            fontWeight = FontWeight.Bold
                        )
                        Text("Advice: ${entry.advice}")
                    }
                }
            }
        }
    }
}
