package com.recapp.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface OpenAiApi {
    @POST("v1/chat/completions")
    suspend fun createChatCompletion(@Body request: ChatRequest): ChatResponse

    @GET("v1/models")
    suspend fun listModels(): ModelListResponse
}
