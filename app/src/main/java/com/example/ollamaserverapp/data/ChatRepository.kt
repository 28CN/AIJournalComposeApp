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


    suspend fun sendPrompt(
        model: String,
        prompt: String,
        stream: Boolean = false
    ): String = withContext(Dispatchers.IO) {
        val json = JSONObject().apply {
            put("model", model)
            put("prompt", prompt)
            put("stream", stream)
        }
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$baseUrl/chat")
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { resp ->
                val resBody = resp.body?.string()
                if (resp.isSuccessful && resBody != null) {
                    JSONObject(resBody).optString("response", "Error: Missing 'response'")
                } else {
                    "Error: ${resp.code}\n${resBody ?: "no body"}"
                }
            }
        } catch (e: Exception) {
            "Exception: ${e.message ?: "unknown error"}"
        }
    }

    suspend fun analyzeJournal(
        prompt: String,
        model: String = "gemma:2b" // or selectedModel
    ): Pair<Emotion, String> = withContext(Dispatchers.IO) {
        // 发送后端期望的 OllamaRequest 结构?????????????
        val json = JSONObject().apply {
            put("model", model)
            put("prompt", prompt)
            put("stream", false) // 非流式，便于一次性拿完整 JSON
        }
        val body = json.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("$baseUrl/analyze")
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { resp ->
                val text = resp.body?.string().orEmpty()

                if (resp.code == 200) {
                    val obj = JSONObject(text)
                    val emo = obj.getString("emotion").uppercase()
                    val adv = obj.getString("advice")
                    val parsed = runCatching { Emotion.valueOf(emo) }
                        .getOrDefault(Emotion.NEUTRAL)
                    parsed to adv
                } else {
                    // 把错误透传到 UI，便于你区分是 422（AI没按格式）
                    Emotion.NEUTRAL to "HTTP ${resp.code}\n${text.ifBlank { "no body" }}"
                }
            }
        } catch (e: Exception) {
            Emotion.NEUTRAL to "EXCEPTION: ${e.message ?: "unknown"}"
        }
    }

    suspend fun getModels(): List<String> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/models")
            .get()
            .build()

        try {
            client.newCall(request).execute().use { resp ->
                val resBody = resp.body?.string()
                if (resp.isSuccessful && resBody != null) {
                    // 简单解析成 List<String>
                    val jsonArray = org.json.JSONArray(resBody)
                    List(jsonArray.length()) { i -> jsonArray.getString(i) }
                } else {
                    emptyList()
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
