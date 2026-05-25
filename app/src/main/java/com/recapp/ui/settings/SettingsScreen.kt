package com.recapp.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val providers = listOf("OpenAI", "OpenAI Compatible", "Claude", "Grok", "Gemini", "DeepSeek", "GLM")
    var providerExpanded by remember { mutableStateOf(false) }
    var modelExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "LLM Configuration",
            style = MaterialTheme.typography.headlineMedium
        )

        // Provider Selection
        ExposedDropdownMenuBox(
            expanded = providerExpanded,
            onExpandedChange = { providerExpanded = !providerExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = viewModel.provider,
                onValueChange = {},
                readOnly = true,
                label = { Text("AI Provider") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = providerExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = providerExpanded,
                onDismissRequest = { providerExpanded = false }
            ) {
                providers.forEach { p ->
                    DropdownMenuItem(
                        text = { Text(text = p) },
                        onClick = {
                            viewModel.updateProvider(p)
                            providerExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = viewModel.endpoint,
            onValueChange = { viewModel.updateEndpoint(it) },
            label = { Text("API Endpoint") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("https://...") }
        )

        OutlinedTextField(
            value = viewModel.apiKey,
            onValueChange = { viewModel.updateApiKey(it) },
            label = { Text("API Key") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            placeholder = { Text("sk-...") }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.testConnection() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Test Connection")
            }
            Button(
                onClick = { viewModel.fetchModels() },
                modifier = Modifier.weight(1f),
                enabled = !viewModel.isFetchingModels
            ) {
                Text(if (viewModel.isFetchingModels) "Fetching..." else "Fetch Models")
            }
        }

        // Model Selection
        ExposedDropdownMenuBox(
            expanded = modelExpanded,
            onExpandedChange = { modelExpanded = !modelExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = viewModel.selectedModel,
                onValueChange = {},
                readOnly = true,
                label = { Text("Selected Model") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modelExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                placeholder = { Text("Select or fetch models") }
            )
            ExposedDropdownMenu(
                expanded = modelExpanded,
                onDismissRequest = { modelExpanded = false }
            ) {
                if (viewModel.availableModels.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("No models fetched") },
                        onClick = { modelExpanded = false }
                    )
                } else {
                    viewModel.availableModels.forEach { model ->
                        DropdownMenuItem(
                            text = { Text(text = model) },
                            onClick = {
                                viewModel.updateSelectedModel(model)
                                modelExpanded = false
                            }
                        )
                    }
                }
            }
        }

        viewModel.testResult?.let { result ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = result,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Button(
            onClick = { viewModel.saveSettings() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Save & Apply")
        }
    }
}
