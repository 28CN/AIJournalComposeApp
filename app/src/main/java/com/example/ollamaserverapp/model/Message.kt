package com.example.ollamaserverapp.model

data class Message(
    val role: Role,
    val content: String
)

enum class Role {
    You, Bot
}