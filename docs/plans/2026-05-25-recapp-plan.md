# Recapp Implementation Plan

> **For Gemini:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build an Android app that records audio in the background, transcribes it on-device, identifies speakers via voice embeddings, and uses a custom LLM endpoint for analysis.

**Architecture:** MVVM and Clean Architecture with Jetpack Compose. Foreground Service for recording. TFLite for Voice Embeddings. Room DB for storage.

**Tech Stack:** Kotlin, Jetpack Compose, Coroutines, Room, Retrofit, TFLite, Android X.

---

### Task 1: Project Initialization

**Files:**
- Create: `build.gradle.kts` (Project root)
- Create: `settings.gradle.kts`
- Create: `app/build.gradle.kts`
- Create: `app/src/main/AndroidManifest.xml`

**Step 1: Write basic project structure script**
Create a basic gradle wrapper setup and Android build files to establish the project skeleton. Include Compose BOM, Room, Retrofit, and TFLite dependencies.

**Step 2: Run build to verify skeleton**
Run: `./gradlew app:assembleDebug`
Expected: PASS (builds an empty APK)

**Step 3: Commit**
`git add . && git commit -m "chore: initialize android project with compose and dependencies"`

---

### Task 2: Implement Permissions and Basic UI Shell

**Files:**
- Modify: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/java/com/recapp/MainActivity.kt`
- Create: `app/src/main/java/com/recapp/ui/theme/Theme.kt`

**Step 1: Add permissions to Manifest**
Add `RECORD_AUDIO`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_MICROPHONE`, `INTERNET`.

**Step 2: Write MainActivity with Permission Request**
Use Compose Accompanist Permissions (or standard ActivityResultContracts) to request Microphone and Notification permissions on startup.

**Step 3: Verify**
Run standard lint or build check.

**Step 4: Commit**
`git add . && git commit -m "feat: add basic UI and permission requests"`

---

### Task 3: Settings Screen & Encrypted Storage

**Files:**
- Create: `app/src/main/java/com/recapp/data/local/SettingsManager.kt`
- Create: `app/src/main/java/com/recapp/ui/settings/SettingsScreen.kt`
- Create: `app/src/main/java/com/recapp/ui/settings/SettingsViewModel.kt`

**Step 1: Implement SettingsManager**
Use `EncryptedSharedPreferences` to store Endpoint, API Key, and selected model.

**Step 2: Create Settings UI**
TextFields for Endpoint and API Key. A Button to test connection (mock for now).

**Step 3: Commit**
`git add . && git commit -m "feat: implement encrypted settings and UI"`

---

### Task 4: Background Recording Service

**Files:**
- Create: `app/src/main/java/com/recapp/service/RecordingService.kt`
- Modify: `app/src/main/AndroidManifest.xml`

**Step 1: Implement Foreground Service**
Create `RecordingService` extending `Service`. Configure a continuous Notification to satisfy Android 14 `microphone` foreground service type requirements.

**Step 2: Implement AudioRecord Loop**
Inside a Coroutine in the service, read from `AudioRecord` into PCM buffers.

**Step 3: Commit**
`git add . && git commit -m "feat: implement foreground recording service"`

---

### Task 5: On-Device STT Integration

**Files:**
- Create: `app/src/main/java/com/recapp/ml/SttEngine.kt`

**Step 1: Wrap SpeechRecognizer**
Implement `SttEngine` that uses `SpeechRecognizer` API to convert audio stream (or chunks) into text.

**Step 2: Commit**
`git add . && git commit -m "feat: add native STT engine wrapper"`

---

### Task 6: TFLite Voice Embeddings (Diarization Mock/Setup)

**Files:**
- Create: `app/src/main/java/com/recapp/ml/VoiceEmbeddingExtractor.kt`

**Step 1: Setup TFLite Interpreter**
Load a dummy or downloaded TFLite model for voice embedding extraction (e.g., a simple dense layer or VGGVox equivalent). Calculate Cosine Similarity.

**Step 2: Commit**
`git add . && git commit -m "feat: add TFLite voice embedding extractor"`

---

### Task 7: Room Database & Speaker Tagging

**Files:**
- Create: `app/src/main/java/com/recapp/data/local/AppDatabase.kt`
- Create: `app/src/main/java/com/recapp/data/local/ConversationDao.kt`

**Step 1: Define Entities**
Create `Conversation`, `SpeakerProfile`, and `AudioSegment` (which holds the text, speaker ID, and embedding).

**Step 2: Commit**
`git add . && git commit -m "feat: setup Room database for conversations and embeddings"`

---

### Task 8: LLM Network Integration

**Files:**
- Create: `app/src/main/java/com/recapp/data/remote/OpenAiApi.kt`
- Create: `app/src/main/java/com/recapp/data/remote/LlmRepository.kt`

**Step 1: Retrofit Setup**
Define the `/v1/models` and `/v1/chat/completions` endpoints. Add interceptor for API Key.

**Step 2: Commit**
`git add . && git commit -m "feat: implement LLM network client"`

---

### Task 9: GitHub Actions CI/CD

**Files:**
- Create: `.github/workflows/android-build.yml`

**Step 1: Write Action YAML**
Configure Ubuntu runner, JDK 17, Checkout, and `./gradlew assembleDebug`.

**Step 2: Commit**
`git add . && git commit -m "ci: add GitHub Actions workflow"`
