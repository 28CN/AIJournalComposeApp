package com.example.ollamaserverapp.model

// transfer emotion to emoji.
fun Emotion.emoji(): String = when (this) {
    Emotion.JOY -> "😊"
    Emotion.SADNESS -> "😢"
    Emotion.ANGER -> "😠"
    Emotion.FEAR -> "😨"
    Emotion.DISGUST -> "🤢"
    Emotion.SURPRISE -> "😮"
    Emotion.NEUTRAL -> "😐"
    Emotion.UNKNOWN -> "❌"
}
