package com.example.ollamaserverapp.model
import java.util.*

data class JournalEntry(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val emotion: Emotion?,
    val advice: String?,
    val createdAt: Long = System.currentTimeMillis()
)

enum class Emotion { JOY, SADNESS, ANGER, FEAR, DISGUST, SURPRISE, NEUTRAL }