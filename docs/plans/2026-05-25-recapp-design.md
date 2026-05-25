# Recapp App Design

## Overview
Recapp is an Android application that records background conversations, transcribes them using on-device STT, separates speakers using a few-shot learning approach with voice embeddings, and uses a user-configured LLM endpoint to summarize and analyze the conversation's intent and nuance.

## 1. System Architecture & Data Flow
The app follows MVVM and Clean Architecture patterns, built with Jetpack Compose.
- **UI Layer (Compose)**: Settings (API Key, Endpoint, Model selection), Recording controls, Conversation list & Speaker tagging UI.
- **Service Layer**: `Foreground Service` maintains microphone permissions and records audio in the background continuously until stopped.
- **AI/ML Layer (On-Device)**:
  - **STT**: Uses Android native `SpeechRecognizer`.
  - **Diarization**: Uses a lightweight `TFLite` model to extract voice embeddings (d-vectors) from audio segments.
- **Data Layer (Room DB)**: Stores recording metadata, STT segments, voice embeddings, speaker tags, and LLM analysis results.
- **Network Layer (Retrofit)**: Handles communication with an OpenAI-compatible REST API for LLM tasks.

## 2. Core Components & ML Logic
- **Background Recording**: Captures raw PCM data using `AudioRecord` within a Foreground Service, ensuring it runs reliably with a persistent notification.
- **Speaker Identification (Few-Shot Learning)**:
  - VAD (Voice Activity Detection) segments the audio.
  - TFLite model extracts embeddings.
  - **Tagging**: Users manually tag speakers ("User A", "User B"). The embeddings are saved to the local database.
  - **Auto-Identification**: New embeddings are compared against the database using Cosine Similarity. If it exceeds a threshold, the speaker is automatically identified, simulating reinforcement learning through user-driven data accumulation.

## 3. LLM Integration, Error Handling & CI/CD
- **LLM Integration**:
  - Endpoint and API keys are stored in `EncryptedSharedPreferences`.
  - The app queries `/v1/models` for available models and uses `/v1/chat/completions` to send the STT transcript for summarization and intent analysis.
- **Error Handling**: Graceful fallback for missing permissions, network errors, or TFLite initialization failures.
- **CI/CD**: GitHub Actions (`.github/workflows/android-build.yml`) automates the build, linting, and testing processes on pushes to the main branch, outputting APK/AAB artifacts.
