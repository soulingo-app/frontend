package com.soulingo.app.ui.screens.auth

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.soulingo.app.ui.components.GlassmorphicCard
import com.soulingo.app.ui.components.GradientButton
import com.soulingo.app.ui.components.PulsingLogo
import com.soulingo.app.ui.components.SouLingoTextField
import com.soulingo.app.ui.theme.SouLingoColors

@Composable
fun AuthScreen(
    onAuthSuccess: (Boolean) -> Unit,
    audioFilePath: String? = null,
    imageUri: String? = null,
    recordingDuration: Long = 0,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState
    val scrollState = rememberScrollState()

    // Recording data'yı ViewModel'e geç
    LaunchedEffect(audioFilePath, imageUri, recordingDuration) {
        if (audioFilePath != null && imageUri != null) {
            viewModel.setRecordingData(
                audioPath = audioFilePath,
                imageUri = imageUri?.let { Uri.parse(it) },
                duration = recordingDuration
            )
        }
    }

    // Auth başarılıysa callback çağır
    LaunchedEffect(uiState.authToken) {
        if (uiState.authToken != null) {
            onAuthSuccess(uiState.userHasRecording)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SouLingoColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            PulsingLogo(
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = if (uiState.isLogin) "Welcome Back" else "Create Account",
                style = MaterialTheme.typography.displayLarge,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = SouLingoColors.DeepIndigo,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (uiState.isLogin)
                    "Sign in to continue your learning journey"
                else
                    "Join SouLingo and start learning today",
                style = MaterialTheme.typography.bodyLarge,
                color = SouLingoColors.TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Form Card
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Username (only for register)
                    if (!uiState.isLogin) {
                        SouLingoTextField(
                            value = uiState.username,
                            onValueChange = { viewModel.updateUsername(it) },
                            label = "Username",
                            isError = uiState.errorMessage?.contains("Username") == true
                        )
                    }

                    // Email
                    SouLingoTextField(
                        value = uiState.email,
                        onValueChange = { viewModel.updateEmail(it) },
                        label = "Email",
                        keyboardType = KeyboardType.Email,
                        isError = uiState.errorMessage?.contains("email") == true
                    )

                    // Password
                    SouLingoTextField(
                        value = uiState.password,
                        onValueChange = { viewModel.updatePassword(it) },
                        label = "Password",
                        isPassword = true,
                        keyboardType = KeyboardType.Password,
                        isError = uiState.errorMessage?.contains("Password") == true
                    )

                    // Confirm Password (only for register)
                    if (!uiState.isLogin) {
                        SouLingoTextField(
                            value = uiState.confirmPassword,
                            onValueChange = { viewModel.updateConfirmPassword(it) },
                            label = "Confirm Password",
                            isPassword = true,
                            keyboardType = KeyboardType.Password,
                            isError = uiState.errorMessage?.contains("match") == true
                        )
                    }

                    // Error Message
                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage ?: "",
                            color = SouLingoColors.Error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            if (!uiState.isLoading) {
                GradientButton(
                    text = if (uiState.isLogin) "Sign In" else "Create Account",
                    onClick = {
                        viewModel.validateAndProceed()
                    }
                )
            } else {
                // Loading indicator
                CircularProgressIndicator(
                    color = SouLingoColors.LuminousMint,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Toggle Auth Mode
            if (!uiState.isLoading) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (uiState.isLogin) "Don't have an account?" else "Already have an account?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = SouLingoColors.TextSecondary
                    )
                    TextButton(
                        onClick = { viewModel.toggleAuthMode() }
                    ) {
                        Text(
                            text = if (uiState.isLogin) "Sign Up" else "Sign In",
                            color = SouLingoColors.LuminousMint,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Note about Gmail integration
            Text(
                text = "Gmail sign-in coming soon",
                style = MaterialTheme.typography.bodyMedium,
                color = SouLingoColors.Neutral,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}