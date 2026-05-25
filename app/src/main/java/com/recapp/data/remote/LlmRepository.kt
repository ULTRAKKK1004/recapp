package com.recapp.data.remote

import com.recapp.data.local.SettingsManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class LlmRepository(private val settingsManager: SettingsManager) {

    private var currentApi: OpenAiApi? = null
    private var cachedEndpoint: String? = null
    private var cachedApiKey: String? = null

    private fun getApi(): OpenAiApi {
        var endpoint = settingsManager.getEndpoint().trim()
        if (endpoint.isBlank()) {
            endpoint = "https://api.openai.com/"
        }
        if (!endpoint.endsWith("/")) {
            endpoint = "$endpoint/"
        }
        
        val apiKey = settingsManager.getApiKey().trim()

        if (currentApi != null && cachedEndpoint == endpoint && cachedApiKey == apiKey) {
            return currentApi!!
        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(endpoint)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        cachedEndpoint = endpoint
        cachedApiKey = apiKey
        currentApi = retrofit.create(OpenAiApi::class.java)
        return currentApi!!
    }

    suspend fun getChatCompletion(messages: List<Message>, model: String): ChatResponse {
        val request = ChatRequest(
            model = model,
            messages = messages
        )
        return getApi().createChatCompletion(request)
    }

    suspend fun getModels(): List<ModelDto> {
        return try {
            val response = getApi().listModels()
            response.data
        } catch (e: Exception) {
            // Fallback for some APIs that might return list directly or different structure
            // For now, let's just rethrow so ViewModel can handle it
            throw e
        }
    }
    
    suspend fun testConnection(): Boolean {
        return try {
            getApi().listModels()
            true
        } catch (e: Exception) {
            false
        }
    }
}
