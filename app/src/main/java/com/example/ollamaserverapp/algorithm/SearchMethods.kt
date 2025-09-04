package com.example.ollamaserverapp.algorithm
import com.example.ollamaserverapp.model.Emotion
import com.example.ollamaserverapp.model.JournalEntry


// ---------- Binary Tree ----------
private class BstNode(
    val key: Emotion,
    val ids: MutableList<String> = mutableListOf(),
    var left: BstNode? = null,
    var right: BstNode? = null
)

private fun bstInsert(root: BstNode?, key: Emotion, id: String): BstNode {
    if (root == null) return BstNode(key, mutableListOf(id))
    when {
        key.name < root.key.name -> root.left = bstInsert(root.left, key, id)
        key.name > root.key.name -> root.right = bstInsert(root.right, key, id)
        else -> root.ids.add(id)
    }
    return root
}

private fun bstFind(root: BstNode?, key: Emotion): List<String> {
    var cur = root
    while (cur != null) {
        when {
            key.name < cur.key.name -> cur = cur.left
            key.name > cur.key.name -> cur = cur.right
            else -> return cur.ids
        }
    }
    return emptyList()
}

fun binaryTreeSearchIds(entries: List<JournalEntry>, target: Emotion): Set<String> {
    var root: BstNode? = null
    for (e in entries) {
        val k = e.emotion ?: Emotion.NEUTRAL
        root = bstInsert(root, k, e.id)
    }
    return bstFind(root, target).toSet()
}

// ---------- HashMap ----------
fun hashMapSearchIds(entries: List<JournalEntry>, target: Emotion): Set<String> {
    val map = mutableMapOf<Emotion, MutableList<String>>()
    for (e in entries) {
        val k = e.emotion ?: Emotion.NEUTRAL
        map.getOrPut(k) { mutableListOf() }.add(e.id)
    }
    return map[target]?.toSet() ?: emptySet()
}

// ---------- Doubly Linked List ----------
private class DNode(val entry: JournalEntry) {
    var prev: DNode? = null
    var next: DNode? = null
}

fun doublyLinkedListSearchIds(entries: List<JournalEntry>, target: Emotion): Set<String> {
    // build
    var head: DNode? = null
    var tail: DNode? = null
    for (e in entries) {
        val node = DNode(e)
        if (head == null) {
            head = node; tail = node
        } else {
            tail!!.next = node
            node.prev = tail
            tail = node
        }
    }
    // traverse
    val ids = mutableSetOf<String>()
    var cur = head
    while (cur != null) {
        val k = cur.entry.emotion ?: Emotion.NEUTRAL
        if (k == target) ids.add(cur.entry.id)
        cur = cur.next
    }
    return ids
}

// Public helpers to get indices instead of ids
fun binaryTreeFindIndices(entries: List<JournalEntry>, target: Emotion): List<Int> {
    val ids = binaryTreeSearchIds(entries, target)
    return entries.withIndex().filter { ids.contains(it.value.id) }.map { it.index }
}

fun hashFindIndices(entries: List<JournalEntry>, target: Emotion): List<Int> {
    val ids = hashMapSearchIds(entries, target)
    return entries.withIndex().filter { ids.contains(it.value.id) }.map { it.index }
}

fun dllFindIndices(entries: List<JournalEntry>, target: Emotion): List<Int> {
    val ids = doublyLinkedListSearchIds(entries, target)
    return entries.withIndex().filter { ids.contains(it.value.id) }.map { it.index }
}

