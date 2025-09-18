package com.example.ollamaserverapp.model
import java.util.*

data class JournalEntry(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val emotion: Emotion?,
    val advice: String?,
    val createdAt: Long = System.currentTimeMillis()
)

//Fixed set of emotions - add UNKNOWN to handle null or error.
enum class Emotion { JOY, SADNESS, ANGER, FEAR, DISGUST, SURPRISE, NEUTRAL, UNKNOWN }