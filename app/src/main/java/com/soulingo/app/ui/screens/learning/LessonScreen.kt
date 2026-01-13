package com.soulingo.app.ui.screens.learning

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.soulingo.app.data.model.Lesson
import com.soulingo.app.ui.components.* // VideoPlayer burada
import com.soulingo.app.ui.theme.SouLingoColors
import kotlinx.coroutines.delay // Delay importu eklendi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LessonScreen(
    lesson: Lesson,
    onComplete: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LessonViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val audioPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    LaunchedEffect(Unit) {
        viewModel.initAudioRecorder(context)
        if (!audioPermissionState.status.isGranted) {
            audioPermissionState.launchPermissionRequest()
        }
    }

    // Video süresi dolduğunda otomatik tamamlama (İsteğe bağlı: ExoPlayer callback'i zaten var ama simülasyonu korudum)
    LaunchedEffect(uiState.isVideoPlaying) {
        if (uiState.isVideoPlaying) {
            // Videonuzun gerçek uzunluğuna göre burayı ayarlayabilir veya
            // VideoPlayer'ın onVideoEnd callback'ine güvenebilirsiniz.
            // Şimdilik mantığı bozmamak için bırakıyorum.
            delay(5000)
            viewModel.onVideoCompleted()
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = SouLingoColors.DeepIndigo
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = lesson.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SouLingoColors.DeepIndigo
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // STEP 1: Video Section (Always Visible)
            Text(
                text = "Step 1: Watch & Listen",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SouLingoColors.DeepIndigo
            )

            Spacer(modifier = Modifier.height(12.dp))

            // GÜNCELLENEN KISIM: VideoPlayerPlaceholder yerine VideoPlayer
            VideoPlayer(
                isPlaying = uiState.isVideoPlaying,
                onVideoEnd = { viewModel.onVideoCompleted() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (!uiState.isVideoPlaying && !uiState.videoCompleted) {
                GradientButton(
                    text = "Play Video",
                    onClick = { viewModel.startVideo() }
                )
            }

            if (uiState.videoCompleted) {
                Text(
                    text = "✓ Video completed",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = SouLingoColors.Success
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // STEP 2: Read Text Section
            Text(
                text = "Step 2: Read the Text",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SouLingoColors.DeepIndigo
            )

            Spacer(modifier = Modifier.height(12.dp))

            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = lesson.content,
                    fontSize = 16.sp,
                    color = SouLingoColors.DeepIndigo,
                    lineHeight = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // STEP 3: Practice Section
            Text(
                text = "Step 3: Practice Speaking",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SouLingoColors.DeepIndigo
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Read the text aloud and record yourself",
                fontSize = 13.sp,
                color = SouLingoColors.TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isEvaluating) {
                CircularProgressIndicator(color = SouLingoColors.LuminousMint)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Evaluating your pronunciation...",
                    fontSize = 14.sp,
                    color = SouLingoColors.TextSecondary
                )
            } else if (uiState.pronunciationResult == null) {
                RecordingButton(
                    isRecording = uiState.isRecording,
                    onClick = { viewModel.toggleRecording() }
                )

                if (uiState.isRecording || uiState.recordingDuration > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                    TimerDisplay(seconds = uiState.recordingDuration)
                }
            }

            // STEP 4: Evaluation Results
            if (uiState.pronunciationResult != null) {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Step 4: Your Result",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SouLingoColors.DeepIndigo
                )

                Spacer(modifier = Modifier.height(12.dp))

                PronunciationResultCard(result = uiState.pronunciationResult!!)

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GradientButton(
                        text = "Try Again",
                        onClick = { viewModel.resetForRetry() },
                        modifier = Modifier.weight(1f)
                    )
                    GradientButton(
                        text = "Complete",
                        onClick = onComplete,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}