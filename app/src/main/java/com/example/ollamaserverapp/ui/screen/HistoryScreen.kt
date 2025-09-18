package com.example.ollamaserverapp.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ollamaserverapp.model.Emotion
import com.example.ollamaserverapp.model.JournalEntry
import com.example.ollamaserverapp.model.emoji
import com.example.ollamaserverapp.presentation.ChatViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(vm: ChatViewModel = viewModel()) {
    val ui by vm.uiState.collectAsState()

    var sortExpanded by remember { mutableStateOf(false) }
    var searchExpanded by remember { mutableStateOf(false) }
    var targetExpanded by remember { mutableStateOf(false) }
    var targetEmotion by remember { mutableStateOf(Emotion.JOY) }

    var trashBounds by remember { mutableStateOf<Rect?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        //  Sort row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExposedDropdownMenuBox(
                expanded = sortExpanded,
                onExpandedChange = { sortExpanded = !sortExpanded }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = ui.sortMethod,
                    onValueChange = {},
                    label = { Text("Sort") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sortExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .weight(1f)
                )
                DropdownMenu(
                    expanded = sortExpanded,
                    onDismissRequest = { sortExpanded = false }
                ) {
                    listOf("Bubble", "Insertion", "Selection").forEach { name ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                vm.onSortMethodSelected(name)
                                sortExpanded = false
                            }
                        )
                    }
                }
            }
            Button(onClick = { vm.sortEntries() }) {
                Text("Sort")
            }
        }

        Spacer(Modifier.height(12.dp))

        //  Search method row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExposedDropdownMenuBox(
                expanded = searchExpanded,
                onExpandedChange = { searchExpanded = !searchExpanded }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = ui.searchMethod,
                    onValueChange = {},
                    label = { Text("Search") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = searchExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .weight(1f)
                )
                DropdownMenu(
                    expanded = searchExpanded,
                    onDismissRequest = { searchExpanded = false }
                ) {
                    listOf("BinaryTree", "HashMap", "DoublyLinkedList").forEach { name ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                vm.onSearchMethodSelected(name)
                                searchExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        //  Target emotion row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExposedDropdownMenuBox(
                expanded = targetExpanded,
                onExpandedChange = { targetExpanded = !targetExpanded }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = targetEmotion.name,
                    onValueChange = {},
                    label = { Text("Target Emotion") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = targetExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .weight(1f)
                )
                DropdownMenu(
                    expanded = targetExpanded,
                    onDismissRequest = { targetExpanded = false }
                ) {
                    Emotion.entries.forEach { emo ->
                        DropdownMenuItem(
                            text = { Text(emo.name) },
                            onClick = {
                                targetEmotion = emo
                                targetExpanded = false
                            }
                        )
                    }
                }
            }
            Button(onClick = { vm.searchByEmotion(targetEmotion) }) {
                Text("Search")
            }
        }

        Spacer(Modifier.height(16.dp))

        //  Journal List + TrashBar
        Box(Modifier.weight(1f).fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ui.entries, key = { it.id }) { entry ->
                    DraggableEntryCard(
                        entry = entry,
                        highlighted = ui.highlightedIds.contains(entry.id),
                        isOverTrash = { rect -> trashBounds?.contains(rect.center) == true },
                        onDroppedOverTrash = { vm.deleteEntry(entry.id) }
                    )
                }
            }

            TrashBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .align(Alignment.BottomCenter)
                    .zIndex(2f)
                    .onGloballyPositioned { coords ->
                        val p = coords.positionInRoot()
                        val s = coords.size
                        trashBounds = Rect(p.x, p.y, p.x + s.width, p.y + s.height)
                    }
            )
        }
    }
}

@Composable
private fun DraggableEntryCard(
    entry: JournalEntry,
    highlighted: Boolean,
    isOverTrash: (Rect) -> Boolean,
    onDroppedOverTrash: () -> Unit
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var dragging by remember { mutableStateOf(false) }
    var bounds by remember { mutableStateOf<Rect?>(null) }

    val border = if (highlighted) {
        BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationX = offset.x
                translationY = offset.y
                if (dragging) shadowElevation = 12f
            }
            .zIndex(if (dragging) 1f else 0f)
            .pointerInput(entry.id) {
                detectDragGestures(
                    onDragStart = { dragging = true },
                    onDragEnd = {
                        dragging = false
                        bounds?.let { rect -> if (isOverTrash(rect)) onDroppedOverTrash() }
                        offset = Offset.Zero
                    },
                    onDragCancel = {
                        dragging = false
                        offset = Offset.Zero
                    }
                ) { _, dragAmount -> offset += dragAmount }
            }
            .onGloballyPositioned {
                val p = it.positionInRoot()
                val s = it.size
                bounds = Rect(p.x, p.y, p.x + s.width, p.y + s.height)
            }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = border
        ) {
            Column(Modifier.padding(12.dp)) {
                val emo = entry.emotion ?: Emotion.NEUTRAL
                Text("${emo.emoji()}  ${emo.name}", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    entry.text,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                if (!entry.advice.isNullOrBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text("Advice: ${entry.advice}", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun TrashBar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text("Drag here to delete", color = Color.Black)
    }
}
