package com.example.ollamaserverapp.algorithm
import com.example.ollamaserverapp.model.Emotion
import com.example.ollamaserverapp.model.JournalEntry

// Return a comparable string from emotion
private fun JournalEntry.emotionKey(): String =
    (emotion ?: Emotion.UNKNOWN).name

// Bubble Sort
fun bubbleSort(list: List<JournalEntry>): List<JournalEntry> {
    val a = mutableListOf<JournalEntry>()
    a.addAll(list)
    for (i in 0 until a.size) {
        for (j in 0 until a.size - i - 1) {
            if (a[j].emotionKey() > a[j + 1].emotionKey()) {
                val tmp = a[j]; a[j] = a[j + 1]; a[j + 1] = tmp
            }
        }
    }
    return a
}

// Insertion Sort
fun insertionSort(list: List<JournalEntry>): List<JournalEntry> {
    val a = mutableListOf<JournalEntry>()
    a.addAll(list)
    for (i in 1 until a.size) {
        val key = a[i]
        var j = i - 1
        while (j >= 0 && a[j].emotionKey() > key.emotionKey()) {
            a[j + 1] = a[j]
            j--
        }
        a[j + 1] = key
    }
    return a
}

// Selection Sort
fun selectionSort(list: List<JournalEntry>): List<JournalEntry> {
    val a = mutableListOf<JournalEntry>()
    a.addAll(list)
    for (i in 0 until a.size) {
        var minIdx = i
        for (j in i + 1 until a.size) {
            if (a[j].emotionKey() < a[minIdx].emotionKey()) minIdx = j
        }
        val tmp = a[i]; a[i] = a[minIdx]; a[minIdx] = tmp
    }
    return a
}