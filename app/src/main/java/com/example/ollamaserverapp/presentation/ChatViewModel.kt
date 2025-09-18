package com.example.ollamaserverapp.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ollamaserverapp.data.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.ollamaserverapp.model.*
import com.example.ollamaserverapp.algorithm.*
import java.util.UUID


data class ChatUiState(
    val input: String = "",
    val entries: List<JournalEntry> = emptyList(),
    val isAnalyzing: Boolean = false,
    val error: String? = null,
    val models: List<String> = emptyList(),
    val selectedModel: String? = null,
    val sortMethod: String = "Bubble",              // set defalut sort method
    val searchMethod: String = "BinaryTree",
    val highlightedIds: Set<String> = emptySet()
)

class ChatViewModel(
    private val repo: ChatRepository = ChatRepository() //
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState


    // Update the input text from UI
    fun onInputChange(text: String) {
        _uiState.value = _uiState.value.copy(input = text)
    }

    // call backend; on success append a new entry; on failure stop loading
    fun send() {
        val prompt = _uiState.value.input.trim()
        if (prompt.isEmpty() || _uiState.value.isAnalyzing) return

        val model = _uiState.value.selectedModel ?: "gemma:2b"

        _uiState.value = _uiState.value.copy(
            isAnalyzing = true,
            input = ""
        )

        viewModelScope.launch {
            try {
                val (emotion, advice) = repo.analyzeJournal(
                    prompt = prompt,
                    model = model
                )

                val newEntry = JournalEntry(
                    // id = UUID.randomUUID().toString(), --could be removed--
                    text = prompt,
                    emotion = emotion,
                    advice = advice
                )

                _uiState.value = _uiState.value.copy(
                    isAnalyzing = false,
                    entries = _uiState.value.entries + newEntry
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isAnalyzing = false)
            }
        }
    }

    // get available models.
    fun loadModels() {
        viewModelScope.launch {
            val list = repo.getModels()
            _uiState.value = _uiState.value.copy(
                models = list,
                selectedModel = list.firstOrNull() //select first model by default
            )
        }
    }

    // store the chosen model for requests
    fun onModelSelected(model: String) {
        Log.d("OllamaDebug", " Sending prompt to $model ... at ${System.currentTimeMillis()}")
        _uiState.value = _uiState.value.copy(selectedModel = model)
    }

    // Update the chosen sorting algorithm name
    fun onSortMethodSelected(name: String) {
        _uiState.value = _uiState.value.copy(sortMethod = name)
    }

    // Apply the chosen sorting algorithm to entries and update state
    fun sortEntries() {
        val sorted = when (_uiState.value.sortMethod) {
            "Bubble" -> bubbleSort(_uiState.value.entries)
            "Insertion" -> insertionSort(_uiState.value.entries)
            "Selection" -> selectionSort(_uiState.value.entries)
            else -> _uiState.value.entries
        }
        _uiState.value = _uiState.value.copy(entries = sorted)
    }

    // Update the chosen searching strategy name
    fun onSearchMethodSelected(name: String) {
        _uiState.value = _uiState.value.copy(searchMethod = name)
    }

    // Run the selected search strategy and collect matching IDs
    fun searchByEmotion(target: Emotion) {
        val entries = _uiState.value.entries
        val matchIds = when (_uiState.value.searchMethod) {
            "BinaryTree" -> binaryTreeSearchIds(entries, target)
            "HashMap" -> hashMapSearchIds(entries, target)
            "DoublyLinkedList" -> doublyLinkedListSearchIds(entries, target)
            else -> emptySet()
        }
        _uiState.value = _uiState.value.copy(highlightedIds = matchIds)
    }

    // Remove one entry by ID (for drag to delete function)
    fun deleteEntry(id: String) {
        val cur = _uiState.value
        _uiState.value = cur.copy(entries = cur.entries.filterNot { it.id == id })
    }
}
