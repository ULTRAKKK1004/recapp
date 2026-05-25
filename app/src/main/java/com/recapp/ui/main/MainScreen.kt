package com.recapp.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MainScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val transcript by viewModel.transcript.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val summary by viewModel.summary.collectAsState()
    val isSummarizing by viewModel.isSummarizing.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "RecApp", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = onNavigateToSettings) {
                Text("Settings")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { viewModel.toggleRecording() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isRecording) "Stop Recording" else "Start Recording")
            }

            Button(
                onClick = { viewModel.summarizeConversation() },
                enabled = !isRecording && transcript.isNotEmpty() && !isSummarizing
            ) {
                Text(if (isSummarizing) "Summarizing..." else "Summarize")
            }
        }

        Text("Transcript:", style = MaterialTheme.typography.titleMedium)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = transcript.ifEmpty { "Waiting for speech..." },
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            )
        }

        if (summary.isNotEmpty()) {
            Text("LLM Summary & Analysis:", style = MaterialTheme.typography.titleMedium)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = summary,
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                )
            }
        }
    }
}
