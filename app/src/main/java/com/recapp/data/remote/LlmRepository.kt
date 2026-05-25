package com.recapp.data.remote

import com.recapp.data.local.SettingsManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LlmRepository(private val settingsManager: SettingsManager) {

    private var currentApi: OpenAiApi? = null
    private var cachedEndpoint: String? = null
    private var cachedApiKey: String? = null

    private fun getApi(): OpenAiApi {
        val endpoint = settingsManager.getEndpoint().let { 
            if (it.endsWith("/")) it else "$it/" 
        }.ifBlank { "https://api.openai.com/" }
        
        val apiKey = settingsManager.getApiKey()

        if (currentApi != null && cachedEndpoint == endpoint && cachedApiKey == apiKey) {
            return currentApi!!
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $apiKey")
                    .build()
                chain.proceed(request)
            }
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
        return getApi().listModels().data
    }
}
