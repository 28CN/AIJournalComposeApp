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

    fun onInputChange(text: String) {
        _uiState.value = _uiState.value.copy(input = text)
    }

    fun send() {
        val prompt = _uiState.value.input.trim()
        if (prompt.isEmpty() || _uiState.value.isAnalyzing) return

        _uiState.value = _uiState.value.copy(
            isAnalyzing = true,
            input = ""
        )

        viewModelScope.launch {
            val (emotion, advice) = repo.analyzeJournal(
                prompt,
                _uiState.value.selectedModel ?: "gemma:2b"
            )
            val newEntry = JournalEntry(
                text = prompt,
                emotion = emotion,
                advice = advice
            )

            _uiState.value = _uiState.value.copy(
                isAnalyzing = false,
                entries = _uiState.value.entries + newEntry
            )
        }
    }

    fun loadModels() {
        viewModelScope.launch {
            val list = repo.getModels()
            _uiState.value = _uiState.value.copy(
                models = list,
                selectedModel = list.firstOrNull() //select first model
            )
        }
    }

    fun onModelSelected(model: String) {
        Log.d("OllamaDebug", " Sending prompt to $model ... at ${System.currentTimeMillis()}")
        _uiState.value = _uiState.value.copy(selectedModel = model)
    }

    fun onSortMethodSelected(name: String) {
        _uiState.value = _uiState.value.copy(sortMethod = name)
    }

    fun sortEntries() {
        val cur = _uiState.value
        val sorted = when (cur.sortMethod) {
            "Insertion" -> insertionSort(cur.entries)
            "Selection" -> selectionSort(cur.entries)
            else -> bubbleSort(cur.entries)
        }
        _uiState.value = cur.copy(entries = sorted)
    }

    fun onSearchMethodSelected(name: String) {
        _uiState.value = _uiState.value.copy(searchMethod = name)
    }

    fun searchByEmotion(target: Emotion) {
        val cur = _uiState.value
        val ids: Set<String> = when (cur.searchMethod) {
            "HashMap" -> hashMapSearchIds(cur.entries, target)
            "DoublyLinkedList" -> doublyLinkedListSearchIds(cur.entries, target)
            else -> binaryTreeSearchIds(cur.entries, target)
        }
        _uiState.value = cur.copy(highlightedIds = ids)
    }


}
