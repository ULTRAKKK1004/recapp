package com.recapp.ui.settings

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.recapp.data.local.SettingsManager
import com.recapp.data.remote.LlmRepository
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsManager = SettingsManager(application)
    private val llmRepository = LlmRepository(settingsManager)

    var provider by mutableStateOf(settingsManager.getProvider())
    var endpoint by mutableStateOf(settingsManager.getEndpoint())
    var apiKey by mutableStateOf(settingsManager.getApiKey())
    var selectedModel by mutableStateOf(settingsManager.getSelectedModel())
    
    val availableModels = mutableStateListOf<String>()
    var isFetchingModels by mutableStateOf(false)
    var testResult by mutableStateOf<String?>(null)

    private val providerEndpoints = mapOf(
        "OpenAI" to "https://api.openai.com/",
        "OpenAI Compatible" to "",
        "Claude" to "https://api.anthropic.com/",
        "Grok" to "https://api.x.ai/",
        "Gemini" to "https://generativelanguage.googleapis.com/",
        "DeepSeek" to "https://api.deepseek.com/",
        "GLM" to "https://open.bigmodel.cn/api/paas/v4/"
    )

    fun updateProvider(newProvider: String) {
        provider = newProvider
        settingsManager.setProvider(newProvider)
        providerEndpoints[newProvider]?.let { 
            if (it.isNotEmpty()) {
                endpoint = it
                settingsManager.setEndpoint(it)
            }
        }
    }

    fun updateEndpoint(newEndpoint: String) {
        endpoint = newEndpoint
        settingsManager.setEndpoint(newEndpoint)
    }

    fun updateApiKey(newApiKey: String) {
        apiKey = newApiKey
        settingsManager.setApiKey(newApiKey)
    }

    fun updateSelectedModel(newModel: String) {
        selectedModel = newModel
        settingsManager.setSelectedModel(newModel)
    }

    fun fetchModels() {
        if (apiKey.isBlank()) {
            testResult = "Please enter an API Key first."
            return
        }

        isFetchingModels = true
        testResult = "Fetching models..."
        availableModels.clear()

        viewModelScope.launch {
            try {
                val models = llmRepository.getModels()
                availableModels.addAll(models.map { it.id })
                if (availableModels.isNotEmpty()) {
                    testResult = "Successfully fetched ${availableModels.size} models."
                } else {
                    testResult = "No models found."
                }
            } catch (e: Exception) {
                testResult = "Error: ${e.message}"
            } finally {
                isFetchingModels = false
            }
        }
    }

    fun testConnection() {
        if (apiKey.isBlank()) {
            testResult = "Please enter an API Key first."
            return
        }

        testResult = "Testing connection..."
        viewModelScope.launch {
            val success = llmRepository.testConnection()
            testResult = if (success) "Connection successful!" else "Connection failed. Check your API Key and Endpoint."
        }
    }

    fun saveSettings() {
        settingsManager.setProvider(provider)
        settingsManager.setEndpoint(endpoint)
        settingsManager.setApiKey(apiKey)
        settingsManager.setSelectedModel(selectedModel)
        testResult = "Settings saved successfully."
    }
}
