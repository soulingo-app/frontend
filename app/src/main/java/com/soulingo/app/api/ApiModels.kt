package com.soulingo.app.api

// Auth Request Models
data class RegisterRequest(
    val email: String,
    val password: String,
    val audio_base64: String? = null,
    val image_base64: String? = null,
    val recording_duration: Long = 0
)

data class LoginRequest(
    val email: String,
    val password: String
)

// Auth Response Models
data class AuthResponse(
    val success: Boolean,
    val message: String? = null,
    val user: UserData? = null,
    val token: String? = null,
    val error: String? = null,
    val errors: Map<String, List<String>>? = null
)

data class UserResponse(
    val success: Boolean,
    val user: UserData? = null,
    val error: String? = null,
    val message: String? = null
)

data class UserData(
    val id: Int,
    val email: String,
    val audio_file_url: String? = null,
    val image_file_url: String? = null,
    val recording_duration: Long? = null,
    val elevenlabs_voice_id: String? = null,
    val did_avatar_id: String? = null,
    val created_at: String? = null
)

// User Update Models
data class UpdateRecordingRequest(
    val audio_base64: String?,
    val image_base64: String?,
    val recording_duration: Long
)