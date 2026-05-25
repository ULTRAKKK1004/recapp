package com.recapp.ui.main

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.recapp.data.local.SettingsManager
import com.recapp.data.remote.LlmRepository
import com.recapp.data.remote.Message
import com.recapp.ml.SttEngine
import com.recapp.service.RecordingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsManager = SettingsManager(application)
    private val llmRepository = LlmRepository(settingsManager)
    val sttEngine = SttEngine()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _summary = MutableStateFlow("")
    val summary: StateFlow<String> = _summary.asStateFlow()

    private val _isSummarizing = MutableStateFlow(false)
    val isSummarizing: StateFlow<Boolean> = _isSummarizing.asStateFlow()

    // Expose STT's transcript directly
    val transcript: StateFlow<String> = sttEngine.transcript

    fun toggleRecording() {
        val app = getApplication<Application>()
        if (_isRecording.value) {
            // Stop recording
            _isRecording.value = false
            sttEngine.stopListening()
            
            val intent = Intent(app, RecordingService::class.java).apply {
                action = RecordingService.ACTION_STOP
            }
            app.startService(intent)
        } else {
            // Start recording
            _isRecording.value = true
            sttEngine.clearTranscript()
            _summary.value = ""
            sttEngine.startListening(app)

            val intent = Intent(app, RecordingService::class.java).apply {
                action = RecordingService.ACTION_START
            }
            // Use standard startService (the service itself handles startForeground if needed)
            app.startService(intent)
        }
    }

    fun summarizeConversation() {
        val currentText = transcript.value
        if (currentText.isBlank()) return

        _isSummarizing.value = true
        _summary.value = "Generating summary..."

        viewModelScope.launch {
            try {
                val model = settingsManager.getSelectedModel().ifBlank { "gpt-3.5-turbo" }
                val messages = listOf(
                    Message(role = "system", content = "You are a helpful assistant. Summarize the following conversation, identify the speakers if possible, and extract the main intentions and nuances."),
                    Message(role = "user", content = currentText)
                )
                
                val response = llmRepository.getChatCompletion(messages, model)
                if (response.choices.isNotEmpty()) {
                    _summary.value = response.choices.first().message.content
                } else {
                    _summary.value = "No summary generated."
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error summarizing", e)
                _summary.value = "Error generating summary: ${e.message}"
            } finally {
                _isSummarizing.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        sttEngine.stopListening()
    }
}
