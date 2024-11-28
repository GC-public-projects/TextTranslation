package com.example.texttranslation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.texttranslation.ui.theme.TextTranslationTheme
import com.google.mlkit.nl.translate.TranslateLanguage
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TextTranslationTheme {
                TranslatorScreen()
            }
        }
    }
}

@Composable
fun TranslatorScreen(viewModel: MyViewModel = viewModel()) {
    val isDownloading by viewModel.isDownloading.collectAsStateWithLifecycle()
    val translatedText by viewModel.translatedText.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val sourceLanguage by viewModel.sourceLanguage.collectAsStateWithLifecycle()
    val targetLanguage by viewModel.targetLanguage.collectAsStateWithLifecycle()

    var sourceText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        // Initialize the translator with your desired languages
        viewModel.initializeTranslator(
            sourceLanguage = TranslateLanguage.ENGLISH,
            targetLanguage = TranslateLanguage.FRENCH
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Source: ${languageName(sourceLanguage)}")
            IconButton(onClick = { viewModel.swapLanguages() }) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Swap Languages"
                )
            }
            Text("Target: ${languageName(targetLanguage)}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = sourceText,
            onValueChange = { sourceText = it; viewModel.translateText(sourceText) },
            label = { Text("Enter Text to Translate") },
            enabled = !isDownloading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isDownloading) {
            Text("Downloading language packs...")
        } else if (!errorMessage.isNullOrEmpty()) {
            Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
        } else if (!translatedText.isNullOrEmpty()) {
            Text("Translation: $translatedText")
        }
    }
}

fun languageName(languageCode: String): String {
    return when (languageCode) {
        TranslateLanguage.ENGLISH -> "English"
        TranslateLanguage.FRENCH -> "French"
        // Add more languages here if needed
        else -> "Unknown"
    }
}
