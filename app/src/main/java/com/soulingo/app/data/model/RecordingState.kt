package com.soulingo.app.data.model

import android.net.Uri

data class RecordingState(
    val isRecording: Boolean = false,
    val recordingDuration: Long = 0,
    val audioFilePath: String? = null,
    val selectedImageUri: Uri? = null,
    val errorMessage: String? = null
)

enum class RecordingStep {
    INSTRUCTION,
    RECORDING,
    COMPLETED
}