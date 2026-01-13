package com.soulingo.app.ui.screens.learning

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soulingo.app.data.model.LearningModule
import com.soulingo.app.data.model.SpanishContent
import com.soulingo.app.ui.components.GlassmorphicCard
import com.soulingo.app.ui.theme.SouLingoColors

@Composable
fun ModuleListScreen(
    onModuleClick: (LearningModule) -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SouLingoColors.Background)
            .padding(24.dp)
    ) {
        // Header with Profile Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Learning Modules",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = SouLingoColors.DeepIndigo
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Choose a module to continue learning",
                    fontSize = 13.sp,
                    color = SouLingoColors.TextSecondary
                )
            }

            // Profile Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SouLingoColors.LuminousMint.copy(alpha = 0.2f))
                    .border(2.dp, SouLingoColors.LuminousMint, CircleShape)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = SouLingoColors.DeepIndigo,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Module List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(SpanishContent.modules) { module ->
                ModuleCard(
                    module = module,
                    onClick = { if (!module.isLocked) onModuleClick(module) }
                )
            }
        }
    }
}

@Composable
fun ModuleCard(
    module: LearningModule,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassmorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = !module.isLocked) { onClick() }
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = module.level,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SouLingoColors.LuminousMint
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = module.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (module.isLocked) SouLingoColors.Neutral else SouLingoColors.DeepIndigo
                    )
                }

                if (module.isLocked) {
                    Text(
                        text = "🔒",
                        fontSize = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${module.lessons.size} lessons",
                        fontSize = 12.sp,
                        color = SouLingoColors.TextSecondary
                    )
                    Text(
                        text = "${module.progress}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SouLingoColors.DeepIndigo
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = module.progress / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = SouLingoColors.LuminousMint,
                    trackColor = SouLingoColors.Neutral.copy(alpha = 0.2f)
                )
            }
        }
    }
}