package com.recapp.ml

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Wrapper around Android's SpeechRecognizer to provide continuous STT capabilities.
 */
class SttEngine {
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    private var shouldContinue = false
    private var context: Context? = null

    private val _transcript = MutableStateFlow("")
    val transcript: StateFlow<String> = _transcript.asStateFlow()

    private val _isListeningFlow = MutableStateFlow(false)
    val isListeningFlow: StateFlow<Boolean> = _isListeningFlow.asStateFlow()

    private val mainHandler = Handler(Looper.getMainLooper())

    fun startListening(context: Context) {
        this.context = context.applicationContext
        mainHandler.post {
            if (isListening) return@post

            shouldContinue = true
            isListening = true
            _isListeningFlow.value = true

            ensureRecognizerAvailable()

            val intent = createRecognizerIntent()
            speechRecognizer?.startListening(intent)
            Log.d("SttEngine", "Started listening")
        }
    }

    fun stopListening() {
        shouldContinue = false
        isListening = false
        _isListeningFlow.value = false
        mainHandler.post {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
            speechRecognizer = null
            Log.d("SttEngine", "Stopped listening and destroyed recognizer")
        }
    }

    fun clearTranscript() {
        _transcript.value = ""
    }

    private fun ensureRecognizerAvailable() {
        if (speechRecognizer == null && context != null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(SttRecognitionListener())
            }
        }
    }

    private fun createRecognizerIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            // Some devices might need this to keep it listening longer
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1000)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1000)
        }
    }

    private inner class SttRecognitionListener : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Log.d("SttEngine", "onReadyForSpeech")
        }

        override fun onBeginningOfSpeech() {
            Log.d("SttEngine", "onBeginningOfSpeech")
        }

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            Log.d("SttEngine", "onEndOfSpeech")
        }

        override fun onError(error: Int) {
            val errorMessage = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                SpeechRecognizer.ERROR_NETWORK -> "Network error"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                SpeechRecognizer.ERROR_NO_MATCH -> "No match"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
                SpeechRecognizer.ERROR_SERVER -> "Error from server"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                else -> "Unknown error"
            }
            Log.e("SttEngine", "onError: $error ($errorMessage)")
            
            isListening = false
            if (shouldContinue) {
                handleRestart()
            } else {
                _isListeningFlow.value = false
            }
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val newText = matches[0]
                val currentText = _transcript.value
                _transcript.value = if (currentText.isEmpty()) newText else "$currentText $newText"
                Log.d("SttEngine", "onResults: $newText")
            }

            isListening = false
            if (shouldContinue) {
                handleRestart()
            } else {
                _isListeningFlow.value = false
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                Log.d("SttEngine", "onPartialResults: ${matches[0]}")
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}

        private fun handleRestart() {
            mainHandler.postDelayed({
                if (shouldContinue) {
                    isListening = true
                    _isListeningFlow.value = true
                    ensureRecognizerAvailable()
                    speechRecognizer?.startListening(createRecognizerIntent())
                    Log.d("SttEngine", "Restarted listening")
                }
            }, 100) // Short delay before restart
        }
    }
}
