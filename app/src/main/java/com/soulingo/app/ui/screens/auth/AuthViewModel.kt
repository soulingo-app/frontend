package com.soulingo.app.ui.screens.auth

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.soulingo.app.api.LoginRequest
import com.soulingo.app.api.RegisterRequest
import com.soulingo.app.api.RetrofitClient
import com.soulingo.app.api.UpdateRecordingRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class AuthUiState(
    val isLogin: Boolean = true,
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val authToken: String? = null,
    val audioFilePath: String? = null,
    val imageUri: String? = null,
    val recordingDuration: Long = 0,
    val userHasRecording: Boolean = false
)

// AndroidViewModel kullan (Application context için)
class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = mutableStateOf(AuthUiState())
    val uiState: State<AuthUiState> = _uiState

    private val contentResolver: ContentResolver = application.contentResolver

    fun toggleAuthMode() {
        _uiState.value = _uiState.value.copy(
            isLogin = !_uiState.value.isLogin,
            errorMessage = null
        )
    }

    fun updateUsername(username: String) {
        _uiState.value = _uiState.value.copy(username = username, errorMessage = null)
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword, errorMessage = null)
    }

    fun setRecordingData(audioPath: String?, imageUri: Uri?, duration: Long) {
        _uiState.value = _uiState.value.copy(
            audioFilePath = audioPath,
            imageUri = imageUri?.toString(),
            recordingDuration = duration
        )
        Log.d("AUTH", "Recording data set: audio=$audioPath, image=$imageUri, duration=$duration")
    }

    fun validateAndProceed() {
        val currentState = _uiState.value

        if (currentState.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            _uiState.value = currentState.copy(errorMessage = "Please enter a valid email address")
            return
        }

        if (currentState.password.length < 6) {
            _uiState.value = currentState.copy(errorMessage = "Password must be at least 6 characters")
            return
        }

        if (!currentState.isLogin && currentState.password != currentState.confirmPassword) {
            _uiState.value = currentState.copy(errorMessage = "Passwords do not match")
            return
        }

        if (!currentState.isLogin && currentState.username.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "Please enter a username")
            return
        }

        if (currentState.isLogin) {
            performLogin()
        } else {
            performRegister()
        }
    }

    private fun performLogin() {
        viewModelScope.launch {
            val currentState = _uiState.value
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

            try {
                Log.d("AUTH", "🔐 Login attempt: ${currentState.email}")

                val request = LoginRequest(
                    email = currentState.email,
                    password = currentState.password
                )

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.login(request)
                }

                if (response.isSuccessful && response.body()?.success == true) {
                    val authResponse = response.body()!!
                    val user = authResponse.user
                    val token = authResponse.token

                    Log.d("AUTH", "✅ Login successful: ${user?.email}")

                    val hasRecording = user?.audio_file_url != null && user?.image_file_url != null

                    if (hasRecording) {
                        Log.d("AUTH", "✅ User has recording - Skip voice recording screen")
                        Log.d("AUTH", "  Audio URL: ${user?.audio_file_url}")
                        Log.d("AUTH", "  Image URL: ${user?.image_file_url}")
                    } else {
                        Log.d("AUTH", "⚠️ User needs to complete voice recording")
                    }

                    _uiState.value = currentState.copy(
                        isLoading = false,
                        authToken = token,
                        userHasRecording = hasRecording,
                        errorMessage = null
                    )
                } else {
                    val errorMsg = response.body()?.error ?: "Login failed"
                    Log.e("AUTH", "❌ Login failed: $errorMsg")
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        errorMessage = errorMsg
                    )
                }
            } catch (e: Exception) {
                Log.e("AUTH", "❌ Login exception: ${e.message}", e)
                _uiState.value = currentState.copy(
                    isLoading = false,
                    errorMessage = "Network error: ${e.message}"
                )
            }
        }
    }

    private fun performRegister() {
        viewModelScope.launch {
            val currentState = _uiState.value
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

            try {
                Log.d("AUTH", "📝 Register attempt: ${currentState.email}")

                val audioBase64 = encodeAudioToBase64(currentState.audioFilePath)
                val imageBase64 = encodeImageToBase64(currentState.imageUri)

                Log.d("AUTH", "📦 Sending registration request:")
                Log.d("AUTH", "  Email: ${currentState.email}")
                Log.d("AUTH", "  Audio: ${if (audioBase64 != null) "✅ ${audioBase64.length} chars" else "❌ null"}")
                Log.d("AUTH", "  Image: ${if (imageBase64 != null) "✅ ${imageBase64.length} chars" else "❌ null"}")
                Log.d("AUTH", "  Duration: ${currentState.recordingDuration}ms")

                val request = RegisterRequest(
                    email = currentState.email,
                    password = currentState.password,
                    audio_base64 = audioBase64,
                    image_base64 = imageBase64,
                    recording_duration = currentState.recordingDuration
                )

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.register(request)
                }

                if (response.isSuccessful && response.body()?.success == true) {
                    val authResponse = response.body()!!
                    val user = authResponse.user
                    val token = authResponse.token

                    Log.d("AUTH", "✅ Registration successful!")
                    Log.d("AUTH", "  Email: ${user?.email}")
                    Log.d("AUTH", "  Audio URL: ${user?.audio_file_url}")
                    Log.d("AUTH", "  Image URL: ${user?.image_file_url}")

                    val hasRecording = user?.audio_file_url != null && user?.image_file_url != null

                    _uiState.value = currentState.copy(
                        isLoading = false,
                        authToken = token,
                        userHasRecording = hasRecording,
                        errorMessage = null
                    )
                } else {
                    val errorMsg = response.body()?.error ?: response.body()?.errors?.toString() ?: "Registration failed"
                    Log.e("AUTH", "❌ Registration failed: $errorMsg")
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        errorMessage = errorMsg
                    )
                }
            } catch (e: Exception) {
                Log.e("AUTH", "❌ Registration exception: ${e.message}", e)
                _uiState.value = currentState.copy(
                    isLoading = false,
                    errorMessage = "Network error: ${e.message}"
                )
            }
        }
    }

    suspend fun updateRecording(
        audioPath: String?,
        imagePath: String?,
        duration: Long
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val token = _uiState.value.authToken ?: return@withContext false

                Log.d("AUTH", "📤 Updating recording...")
                Log.d("AUTH", "  Audio path: $audioPath")
                Log.d("AUTH", "  Image path: $imagePath")
                Log.d("AUTH", "  Duration: $duration ms")

                val audioBase64 = encodeAudioToBase64(audioPath)
                val imageBase64 = encodeImageToBase64(imagePath)

                val request = UpdateRecordingRequest(
                    audio_base64 = audioBase64,
                    image_base64 = imageBase64,
                    recording_duration = duration
                )

                val response = RetrofitClient.instance.updateRecording(
                    token = "Bearer $token",
                    request = request
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    Log.d("AUTH", "✅ Recording updated successfully!")
                    true
                } else {
                    Log.e("AUTH", "❌ Update failed: ${response.code()} - ${response.message()}")
                    false
                }
            } catch (e: Exception) {
                Log.e("AUTH", "❌ Update exception: ${e.message}", e)
                false
            }
        }
    }

    // Helper function - Audio encoding
    private fun encodeAudioToBase64(audioPath: String?): String? {
        return audioPath?.let { path ->
            try {
                val audioFile = File(path)
                if (audioFile.exists()) {
                    val audioBytes = audioFile.readBytes()
                    Base64.encodeToString(audioBytes, Base64.NO_WRAP).also {
                        Log.d("AUTH", "✅ Audio encoded: ${it.length} chars")
                    }
                } else {
                    Log.e("AUTH", "❌ Audio file not found: $path")
                    null
                }
            } catch (e: Exception) {
                Log.e("AUTH", "❌ Audio encoding error: ${e.message}")
                null
            }
        }
    }

    // Helper function - Image encoding (ContentResolver kullanarak)
    private fun encodeImageToBase64(imagePath: String?): String? {
        return imagePath?.let { uriString ->
            try {
                val uri = Uri.parse(uriString)
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    val imageBytes = inputStream.readBytes()
                    Base64.encodeToString(imageBytes, Base64.NO_WRAP).also {
                        Log.d("AUTH", "✅ Image encoded: ${it.length} chars")
                    }
                } ?: run {
                    Log.e("AUTH", "❌ Could not open input stream for URI: $uriString")
                    null
                }
            } catch (e: Exception) {
                Log.e("AUTH", "❌ Image encoding error: ${e.message}")
                null
            }
        }
    }
}