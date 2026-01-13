package com.soulingo.app.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soulingo.app.data.model.OnboardingPage
import com.soulingo.app.ui.components.AnimatedSouLingoLogo
import com.soulingo.app.ui.components.GlassmorphicCard
import com.soulingo.app.ui.components.GradientButton
import com.soulingo.app.ui.components.PulsingLogo
import com.soulingo.app.ui.theme.SouLingoColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = listOf(
        OnboardingPage(
            title = "Welcome to SouLingo",
            description = "Learn languages with your own voice and face using AI technology"
        ),
        OnboardingPage(
            title = "Your Voice, Your Lessons",
            description = "AI clones your voice and delivers educational content in your own voice"
        ),
        OnboardingPage(
            title = "Your Face, Your Avatar",
            description = "Create your digital twin and learn with your personalized avatar"
        ),
        OnboardingPage(
            title = "Voice Recording",
            description = "Record a few simple sentences. It only takes 30-60 seconds"
        ),
        OnboardingPage(
            title = "How It Works",
            description = "4-stage pedagogical cycle: Listen → Repeat → Understand → Speak"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SouLingoColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skip Button
            if (pagerState.currentPage < pages.size - 1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pages.size - 1)
                        }
                    }) {
                        Text(
                            text = "Skip",
                            color = SouLingoColors.TextSecondary
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(48.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                OnboardingPageContent(
                    page = pages[page],
                    isFirstPage = page == 0
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Page Indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pages.size) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (index == pagerState.currentPage) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == pagerState.currentPage)
                                    SouLingoColors.LuminousMint
                                else
                                    SouLingoColors.Neutral.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Navigation Buttons
            GradientButton(
                text = if (pagerState.currentPage == pages.size - 1) "Get Started" else "Continue",
                onClick = {
                    if (pagerState.currentPage == pages.size - 1) {
                        onFinished()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    isFirstPage: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GlassmorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Show animated logo on first page, pulsing logo on others
                if (isFirstPage) {
                    AnimatedSouLingoLogo(
                        modifier = Modifier.size(200.dp)
                    )
                } else {
                    PulsingLogo(
                        modifier = Modifier.size(180.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = page.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SouLingoColors.DeepIndigo,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = SouLingoColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}