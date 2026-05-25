package com.recapp.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SettingsManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_settings",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_ENDPOINT = "endpoint"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_SELECTED_MODEL = "selected_model"
    }

    fun getEndpoint(): String = sharedPreferences.getString(KEY_ENDPOINT, "") ?: ""
    fun setEndpoint(endpoint: String) = sharedPreferences.edit().putString(KEY_ENDPOINT, endpoint).apply()

    fun getApiKey(): String = sharedPreferences.getString(KEY_API_KEY, "") ?: ""
    fun setApiKey(apiKey: String) = sharedPreferences.edit().putString(KEY_API_KEY, apiKey).apply()

    fun getSelectedModel(): String = sharedPreferences.getString(KEY_SELECTED_MODEL, "") ?: ""
    fun setSelectedModel(model: String) = sharedPreferences.edit().putString(KEY_SELECTED_MODEL, model).apply()
}
