package com.soulingo.app.ui.screens.recording

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.soulingo.app.data.model.RecordingStep
import com.soulingo.app.ui.components.GradientButton
import com.soulingo.app.ui.components.RecordingButton
import com.soulingo.app.ui.components.TimerDisplay
import com.soulingo.app.ui.theme.SouLingoColors

const val RECORDING_TEXT = "Today is a good day. I am learning something new."

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VoiceRecordingScreen(
    onComplete: (String?, Uri?, Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VoiceRecordingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentStep by viewModel.currentStep.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.updateSelectedImage(uri)
    }

    LaunchedEffect(Unit) {
        viewModel.initAudioRecorder(context)
        permissionsState.launchMultiplePermissionRequest()
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
            // Title
            Text(
                text = "Setup Your Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = SouLingoColors.DeepIndigo,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Record your voice and upload your photo",
                fontSize = 14.sp,
                color = SouLingoColors.TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Profile Image (Center)
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(
                        if (uiState.selectedImageUri != null)
                            Color.Transparent
                        else
                            SouLingoColors.LuminousMint.copy(alpha = 0.2f)
                    )
                    .border(
                        width = 4.dp,
                        color = SouLingoColors.LuminousMint,
                        shape = CircleShape
                    )
                    .clickable {
                        if (permissionsState.allPermissionsGranted) {
                            imagePickerLauncher.launch("image/*")
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.selectedImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(uiState.selectedImageUri),
                        contentDescription = "Profile photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "Tap to upload photo",
                        color = SouLingoColors.TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (uiState.selectedImageUri != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "✓ Photo Selected",
                    color = SouLingoColors.Success,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Voice Recording Section (Center)
            if (currentStep == RecordingStep.INSTRUCTION || currentStep == RecordingStep.RECORDING) {
                Text(
                    text = "Read aloud:",
                    fontSize = 14.sp,
                    color = SouLingoColors.TextSecondary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "\"$RECORDING_TEXT\"",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = SouLingoColors.DeepIndigo,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Recording Button
            RecordingButton(
                isRecording = uiState.isRecording,
                onClick = { viewModel.toggleRecording() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Timer
            if (uiState.isRecording || currentStep == RecordingStep.COMPLETED) {
                TimerDisplay(seconds = uiState.recordingDuration)
            }

            // Status
            if (currentStep == RecordingStep.COMPLETED) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "✓ Recording Complete",
                    color = SouLingoColors.Success,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Continue Button
            GradientButton(
                text = "Complete Setup",
                onClick = {
                    onComplete(
                        uiState.audioFilePath,
                        uiState.selectedImageUri,
                        uiState.recordingDuration
                    )
                },
                enabled = viewModel.canProceed()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Minimum 3 seconds recording required",
                fontSize = 11.sp,
                color = SouLingoColors.Neutral,
                textAlign = TextAlign.Center
            )
        }
    }
}