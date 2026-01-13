package com.soulingo.app.ui.screens.profile

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import coil.compose.rememberAsyncImagePainter
import com.soulingo.app.ui.components.GradientButton
import com.soulingo.app.ui.theme.SouLingoColors

@Composable
fun ProfileScreen(
    audioFilePath: String?,
    imageUri: String?,
    recordingDuration: Long,
    onStartLearning: () -> Unit,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var isPlayingAudio by remember { mutableStateOf(false) }
    val mediaPlayer = remember { MediaPlayer() }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
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
            // Back button (optional)
            if (showBackButton) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onStartLearning) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = SouLingoColors.DeepIndigo
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Title
            Text(
                text = "Your Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = SouLingoColors.DeepIndigo,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your personalized learning profile",
                fontSize = 13.sp,
                color = SouLingoColors.TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Your Avatar Section
            Text(
                text = "Your Avatar",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = SouLingoColors.DeepIndigo
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Profile Image
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .border(
                        width = 3.dp,
                        color = SouLingoColors.LuminousMint,
                        shape = CircleShape
                    )
                    .background(SouLingoColors.Surface),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null && imageUri != "null") {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Profile photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your digital twin is ready",
                fontSize = 12.sp,
                color = SouLingoColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Your Voice Section
            Text(
                text = "Your Voice",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = SouLingoColors.DeepIndigo
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Recording Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Duration",
                        fontSize = 12.sp,
                        color = SouLingoColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format("%02d:%02d", recordingDuration / 60, recordingDuration % 60),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SouLingoColors.DeepIndigo
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Status",
                        fontSize = 12.sp,
                        color = SouLingoColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ready",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SouLingoColors.Success
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Play Audio Button
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(
                        if (isPlayingAudio)
                            SouLingoColors.Error
                        else
                            SouLingoColors.LuminousMint
                    )
                    .clickable {
                        if (audioFilePath != null) {
                            if (isPlayingAudio) {
                                mediaPlayer.pause()
                                isPlayingAudio = false
                            } else {
                                try {
                                    mediaPlayer.reset()
                                    mediaPlayer.setDataSource(audioFilePath)
                                    mediaPlayer.prepare()
                                    mediaPlayer.start()
                                    isPlayingAudio = true

                                    mediaPlayer.setOnCompletionListener {
                                        isPlayingAudio = false
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isPlayingAudio) "⏸" else "▶",
                    fontSize = 28.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isPlayingAudio) "Playing..." else "Tap to play",
                fontSize = 12.sp,
                color = SouLingoColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Learning Progress
            Text(
                text = "Learning Progress",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = SouLingoColors.DeepIndigo
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your learning journey will be tracked here",
                fontSize = 12.sp,
                color = SouLingoColors.TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Start Learning Button
            GradientButton(
                text = if (showBackButton) "Back to Learning" else "Start Learning",
                onClick = onStartLearning
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}