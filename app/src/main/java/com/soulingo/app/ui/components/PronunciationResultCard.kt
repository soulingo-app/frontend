package com.soulingo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soulingo.app.data.model.PronunciationResult
import com.soulingo.app.ui.theme.SouLingoColors

@Composable
fun PronunciationResultCard(
    result: PronunciationResult,
    modifier: Modifier = Modifier
) {
    GlassmorphicCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Score Circle
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            result.score >= 90 -> SouLingoColors.Success
                            result.score >= 70 -> SouLingoColors.LuminousMint
                            else -> SouLingoColors.Error
                        }.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${result.score}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            result.score >= 90 -> SouLingoColors.Success
                            result.score >= 70 -> SouLingoColors.DeepIndigo
                            else -> SouLingoColors.Error
                        }
                    )
                    Text(
                        text = "/100",
                        fontSize = 14.sp,
                        color = SouLingoColors.TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Feedback
            Text(
                text = result.feedback,
                fontSize = 14.sp,
                color = SouLingoColors.TextSecondary,
                textAlign = TextAlign.Center
            )

            // Mistakes
            if (result.mistakes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Areas to Improve:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SouLingoColors.DeepIndigo
                )

                Spacer(modifier = Modifier.height(8.dp))

                result.mistakes.forEach { mistake ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SouLingoColors.Error.copy(alpha = 0.1f))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "\"${mistake.word}\"",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SouLingoColors.DeepIndigo
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Expected: ${mistake.expectedPronunciation}",
                                fontSize = 12.sp,
                                color = SouLingoColors.Success
                            )
                            Text(
                                text = "You said: ${mistake.actualPronunciation}",
                                fontSize = 12.sp,
                                color = SouLingoColors.Error
                            )
                        }
                    }
                }
            }
        }
    }
}