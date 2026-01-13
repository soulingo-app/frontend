package com.soulingo.app.ui.screens.recording

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soulingo.app.data.model.RecordingState
import com.soulingo.app.data.model.RecordingStep
import com.soulingo.app.utils.AudioRecorder
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

class VoiceRecordingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RecordingState())
    val uiState = _uiState.asStateFlow()

    private val _currentStep = MutableStateFlow(RecordingStep.INSTRUCTION)
    val currentStep = _currentStep.asStateFlow()

    private var timerJob: Job? = null
    private var audioRecorder: AudioRecorder? = null

    fun initAudioRecorder(context: Context) {
        audioRecorder = AudioRecorder(context)
    }

    fun startRecording() {
        val filePath = audioRecorder?.startRecording()
        if (filePath != null) {
            _uiState.value = _uiState.value.copy(
                isRecording = true,
                recordingDuration = 0,
                audioFilePath = filePath,
                errorMessage = null
            )
            _currentStep.value = RecordingStep.RECORDING
            startTimer()
        } else {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Failed to start recording"
            )
        }
    }

    fun stopRecording() {
        val filePath = audioRecorder?.stopRecording()
        if (filePath != null) {
            _uiState.value = _uiState.value.copy(
                isRecording = false,
                audioFilePath = filePath
            )
            stopTimer()
            _currentStep.value = RecordingStep.COMPLETED
        } else {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Failed to stop recording",
                isRecording = false
            )
            stopTimer()
        }
    }

    fun toggleRecording() {
        if (_uiState.value.isRecording) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    fun updateSelectedImage(uri: Uri?) {
        _uiState.value = _uiState.value.copy(selectedImageUri = uri)
    }

    fun canProceed(): Boolean {
        val state = _uiState.value
        Log.d("VoiceRecordingVM", "canProceed check:")
        Log.d("VoiceRecordingVM", "  isRecording: ${state.isRecording}")
        Log.d("VoiceRecordingVM", "  duration: ${state.recordingDuration}")
        Log.d("VoiceRecordingVM", "  imageUri: ${state.selectedImageUri}")
        Log.d("VoiceRecordingVM", "  currentStep: ${_currentStep.value}")

        return state.recordingDuration >= 3 &&
                state.selectedImageUri != null
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.isRecording) {
                delay(1000)
                _uiState.value = _uiState.value.copy(
                    recordingDuration = _uiState.value.recordingDuration + 1
                )
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        audioRecorder?.release()
    }
}