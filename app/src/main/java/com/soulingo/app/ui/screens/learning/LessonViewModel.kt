package com.soulingo.app.ui.screens.learning

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soulingo.app.data.model.Lesson
import com.soulingo.app.data.model.PronunciationMistake
import com.soulingo.app.data.model.PronunciationResult
import com.soulingo.app.utils.AudioRecorder
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LessonUiState(
    val currentStep: LessonStep = LessonStep.WATCH_VIDEO,
    val isVideoPlaying: Boolean = false,
    val videoCompleted: Boolean = false,
    val isRecording: Boolean = false,
    val recordingDuration: Long = 0,
    val audioFilePath: String? = null,
    val pronunciationResult: PronunciationResult? = null,
    val isEvaluating: Boolean = false
)

enum class LessonStep {
    WATCH_VIDEO,      // Video izleme
    READ_TEXT,        // Metin okuma
    RECORD_PRACTICE,  // Ses kaydı
    EVALUATION        // Değerlendirme
}

class LessonViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LessonUiState())
    val uiState = _uiState.asStateFlow()

    private var audioRecorder: AudioRecorder? = null
    private var timerJob: Job? = null

    fun initAudioRecorder(context: Context) {
        audioRecorder = AudioRecorder(context)
    }

    fun onVideoCompleted() {
        _uiState.value = _uiState.value.copy(
            videoCompleted = true,
            isVideoPlaying = false
        )
    }
    fun startVideo() {
        _uiState.value = _uiState.value.copy(isVideoPlaying = true)
    }

    fun proceedToReadText() {
        _uiState.value = _uiState.value.copy(
            currentStep = LessonStep.READ_TEXT
        )
    }

    fun proceedToRecording() {
        _uiState.value = _uiState.value.copy(
            currentStep = LessonStep.RECORD_PRACTICE
        )
    }

    fun startRecording() {
        val filePath = audioRecorder?.startRecording()
        if (filePath != null) {
            _uiState.value = _uiState.value.copy(
                isRecording = true,
                recordingDuration = 0,
                audioFilePath = filePath
            )
            startTimer()
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
            evaluatePronunciation()
        }
    }

    fun toggleRecording() {
        if (_uiState.value.isRecording) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    private fun evaluatePronunciation() {
        _uiState.value = _uiState.value.copy(isEvaluating = true)

        viewModelScope.launch {
            // Simüle edilmiş değerlendirme (3 saniye bekle)
            delay(3000)

            // Dummy pronunciation result
            val result = PronunciationResult(
                score = (70..95).random(),
                mistakes = listOf(
                    PronunciationMistake(
                        word = "estudiante",
                        expectedPronunciation = "es-tu-di-AN-te",
                        actualPronunciation = "es-tu-di-an-TE",
                        timestamp = 1500
                    )
                ),
                feedback = "Good pronunciation! Pay attention to the stress on 'estudiante'."
            )

            _uiState.value = _uiState.value.copy(
                isEvaluating = false,
                pronunciationResult = result,
                currentStep = LessonStep.EVALUATION
            )
        }
    }

    fun resetLesson() {
        _uiState.value = LessonUiState()
    }
    fun resetForRetry() {
        _uiState.value = _uiState.value.copy(
            isRecording = false,
            recordingDuration = 0,
            audioFilePath = null,
            pronunciationResult = null,
            isEvaluating = false
        )
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