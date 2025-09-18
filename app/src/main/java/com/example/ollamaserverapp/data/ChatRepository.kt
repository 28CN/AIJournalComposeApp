package com.example.ollamaserverapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import com.example.ollamaserverapp.model.Emotion

class ChatRepository(
    private val baseUrl: String = "http://10.0.2.2:8080" //
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .build()

    suspend fun analyzeJournal(
        prompt: String,
        model: String = "gemma:2b" // or selectedModel
    ): Pair<Emotion, String> = withContext(Dispatchers.IO) {
        // Send the expected OllamaRequest structure to the backend
        val json = JSONObject().apply {
            put("model", model)
            put("prompt", prompt)
            put("stream", false)
        }
        val body = json.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("$baseUrl/analyze")
            .post(body)
            .build()

        // Send journal entry to backend for emotion analysis
        try {
            client.newCall(request).execute().use { resp ->
                val text = resp.body?.string().orEmpty()

                if (resp.code == 200) {
                    val obj = JSONObject(text)
                    val emo = obj.getString("emotion").uppercase()
                    val adv = obj.getString("advice")
                    val parsed = runCatching { Emotion.valueOf(emo) }
                        .getOrDefault(Emotion.UNKNOWN)
                    parsed to adv
                } else {
                    // Pass the error through to the UI to check if it's 422 error.
                    Emotion.UNKNOWN to "HTTP ${resp.code}\n${text.ifBlank { "no body" }}"
                }
            }
        } catch (e: Exception) {
            Emotion.UNKNOWN to "EXCEPTION: ${e.message ?: "unknown"}"
        }
    }

    // get models list
    suspend fun getModels(): List<String> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/models")
            .get()
            .build()

        try {
            client.newCall(request).execute().use { resp ->
                val resBody = resp.body?.string()
                if (resp.isSuccessful && resBody != null) {
                    // List<String>
                    val jsonArray = org.json.JSONArray(resBody)
                    List(jsonArray.length()) { i -> jsonArray.getString(i) }
                } else {
                    emptyList()
                }
            }
        } catch (e: Exception) {
            return@withContext emptyList()
        }
    }
}
