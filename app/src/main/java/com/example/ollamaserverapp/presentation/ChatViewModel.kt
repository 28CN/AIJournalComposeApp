package com.example.ollamaserverapp.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ollamaserverapp.data.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.ollamaserverapp.model.*

data class ChatUiState(
    val input: String = "",
    val messages: List<Message> = emptyList(),
    val isSending: Boolean = false,
    val models: List<String> = emptyList(),
    val selectedModel: String? = null
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
        if (prompt.isEmpty() || _uiState.value.isSending) return

        _uiState.value = _uiState.value.copy(
            isSending = true,
            input = "",
            messages = _uiState.value.messages + Message(Role.You, prompt)
        )

        viewModelScope.launch {
            val res = repo.sendPrompt(_uiState.value.selectedModel ?: "", prompt, stream = false)
            _uiState.value = _uiState.value.copy(
                isSending = false,
                messages = _uiState.value.messages + Message(Role.Bot, res)
            )
        }
    }

    fun loadModels() {
        viewModelScope.launch {
            val list = repo.getModels()
            _uiState.value = _uiState.value.copy(
                models = list,
                selectedModel = list.firstOrNull() // 默认选第一个
            )
        }
    }

    fun onModelSelected(model: String) {
        Log.d("OllamaDebug", " Sending prompt to $model ... at ${System.currentTimeMillis()}")
        _uiState.value = _uiState.value.copy(selectedModel = model)
    }
}
