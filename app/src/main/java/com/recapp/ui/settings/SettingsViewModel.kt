package com.recapp.ui.settings

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.recapp.data.local.SettingsManager

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsManager = SettingsManager(application)

    var endpoint by mutableStateOf(settingsManager.getEndpoint())
        private set

    var apiKey by mutableStateOf(settingsManager.getApiKey())
        private set

    var selectedModel by mutableStateOf(settingsManager.getSelectedModel())
        private set

    fun updateEndpoint(newEndpoint: String) {
        endpoint = newEndpoint
    }

    fun updateApiKey(newApiKey: String) {
        apiKey = newApiKey
    }

    fun updateSelectedModel(newModel: String) {
        selectedModel = newModel
    }

    fun saveSettings() {
        settingsManager.setEndpoint(endpoint)
        settingsManager.setApiKey(apiKey)
        settingsManager.setSelectedModel(selectedModel)
    }
}
