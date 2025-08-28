package com.example.ollamaserverapp.model

// Small helpers for UI and parsing.
fun Emotion.emoji(): String = when (this) {
    Emotion.JOY -> "😊"
    Emotion.SADNESS -> "😢"
    Emotion.ANGER -> "😠"
    Emotion.FEAR -> "😨"
    Emotion.DISGUST -> "🤢"
    Emotion.SURPRISE -> "😮"
    Emotion.NEUTRAL -> "😐"
}

fun emotionFromString(s: String): Emotion? = when (s.trim().uppercase()) {
    "JOY" -> Emotion.JOY
    "SADNESS" -> Emotion.SADNESS
    "ANGER" -> Emotion.ANGER
    "FEAR" -> Emotion.FEAR
    "DISGUST" -> Emotion.DISGUST
    "SURPRISE" -> Emotion.SURPRISE
    "NEUTRAL" -> Emotion.NEUTRAL
    else -> null
}
