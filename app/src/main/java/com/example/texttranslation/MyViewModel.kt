package com.example.texttranslation

import androidx.lifecycle.ViewModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class MyViewModel : ViewModel()  {
    var translator : Translator? = null

    private val _translatedText = MutableStateFlow<String?>(null)
    val translatedText: StateFlow<String?> = _translatedText

    private val _sourceLanguage = MutableStateFlow(TranslateLanguage.ENGLISH)
    val sourceLanguage: StateFlow<String> = _sourceLanguage

    private val _targetLanguage = MutableStateFlow(TranslateLanguage.SPANISH)
    val targetLanguage: StateFlow<String> = _targetLanguage

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading: StateFlow<Boolean> = _isDownloading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun initializeTranslator(sourceLanguage: String, targetLanguage: String) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage)
            .setTargetLanguage(targetLanguage)
            .build()
        translator = Translation.getClient(options)

        downloadLanguagePack()
    }

    private fun downloadLanguagePack() {
        val currentTranslator = translator ?: return
        _isDownloading.value = true

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        currentTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener { _isDownloading.value = false }
            .addOnFailureListener { exception ->
                _isDownloading.value = false
                _errorMessage.value = exception.message
            }
    }

    fun translateText(input: String) {
        val currentTranslator = translator ?: return

        currentTranslator.translate(input)
            .addOnSuccessListener { translated -> _translatedText.value = translated }
            .addOnFailureListener { exception -> _errorMessage.value = exception.message }
    }

    fun swapLanguages() {
        val newSource = _targetLanguage.value
        val newTarget = _sourceLanguage.value

        _sourceLanguage.update { newSource }
        _targetLanguage.update { newTarget }

        initializeTranslator(_sourceLanguage.value, _targetLanguage.value) // Reinitialize with swapped languages
    }

    override fun onCleared() {
        super.onCleared()
        translator?.close()
    }
}