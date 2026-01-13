package com.soulingo.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soulingo.app.ui.theme.SouLingoColors

@Composable
fun RecordingButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "recording_pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .size(120.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                brush = if (isRecording) {
                    Brush.radialGradient(
                        colors = listOf(
                            SouLingoColors.Error,
                            SouLingoColors.Error.copy(alpha = 0.7f)
                        )
                    )
                } else {
                    Brush.radialGradient(
                        colors = listOf(
                            SouLingoColors.LuminousMint,
                            SouLingoColors.MintVariant
                        )
                    )
                }
            )
            .border(
                width = 4.dp,
                color = Color.White,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isRecording) "🔴" else "🎙️",
                fontSize = 40.sp
            )
            Text(
                text = if (isRecording) "Stop" else "Record",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun TimerDisplay(
    seconds: Long,
    modifier: Modifier = Modifier
) {
    Text(
        text = String.format("%02d:%02d", seconds / 60, seconds % 60),
        style = MaterialTheme.typography.displayLarge,
        fontSize = 48.sp,
        color = SouLingoColors.DeepIndigo,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}