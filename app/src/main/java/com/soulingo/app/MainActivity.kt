package com.soulingo.app

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.soulingo.app.api.RetrofitClient
import com.soulingo.app.data.model.LearningModule
import com.soulingo.app.data.model.Lesson
import com.soulingo.app.data.model.LessonType
import com.soulingo.app.ui.components.GlassmorphicCard
import com.soulingo.app.ui.screens.auth.AuthScreen
import com.soulingo.app.ui.screens.auth.AuthViewModel
import com.soulingo.app.ui.screens.learning.LessonScreen
import com.soulingo.app.ui.screens.learning.ModuleListScreen
import com.soulingo.app.ui.screens.onboarding.OnboardingScreen
import com.soulingo.app.ui.screens.profile.ProfileScreen
import com.soulingo.app.ui.screens.recording.VoiceRecordingScreen
import com.soulingo.app.ui.theme.SouLingoColors
import com.soulingo.app.ui.theme.SouLingoTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // ============ API TEST - BAŞLANGIÇ ============
            LaunchedEffect(Unit) {
                try {
                    Log.d("API_TEST", "🚀 Starting API call to backend...")
                    val response = RetrofitClient.instance.getLessons()

                    if (response.isSuccessful) {
                        val lessons = response.body()
                        Log.d("API_TEST", "✅ SUCCESS! Total lessons: ${lessons?.size}")
                        lessons?.forEach { lesson ->
                            Log.d("API_TEST", "📚 Lesson: ${lesson.title} (ID: ${lesson.lessonId})")
                        }
                    } else {
                        Log.e("API_TEST", "❌ HTTP Error: ${response.code()} - ${response.message()}")
                        Log.e("API_TEST", "Error body: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e("API_TEST", "💥 Exception occurred: ${e.javaClass.simpleName}")
                    Log.e("API_TEST", "Message: ${e.message}")
                    e.printStackTrace()
                }
            }
            // ============ API TEST - BİTİŞ ============

            SouLingoTheme {
                // Navigation state
                var showOnboarding by remember { mutableStateOf(true) }
                var isAuthenticated by remember { mutableStateOf(false) }
                var userHasRecording by remember { mutableStateOf(false) }
                var isRecordingComplete by remember { mutableStateOf(false) }
                var hasStartedLearning by remember { mutableStateOf(false) }

                // Recording data
                var audioFilePath by remember { mutableStateOf<String?>(null) }
                var imageUri by remember { mutableStateOf<Uri?>(null) }
                var recordingDuration by remember { mutableStateOf(0L) }

                // Learning navigation
                var selectedModule by remember { mutableStateOf<LearningModule?>(null) }
                var selectedLesson by remember { mutableStateOf<Lesson?>(null) }
                var showProfile by remember { mutableStateOf(false) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when {
                        showOnboarding -> {
                            OnboardingScreen(
                                onFinished = {
                                    showOnboarding = false
                                    Log.d("MAIN", "✅ Onboarding completed")
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        !isAuthenticated -> {
                            AuthScreen(
                                onAuthSuccess = { hasRecording ->
                                    isAuthenticated = true
                                    userHasRecording = hasRecording
                                    Log.d("MAIN", "✅ Auth successful! User has recording: $hasRecording")
                                },
                                audioFilePath = audioFilePath,
                                imageUri = imageUri?.toString(),
                                recordingDuration = recordingDuration,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        // Auth'dan sonra: Eğer kullanıcının ses/fotoğrafı yoksa
                        isAuthenticated && !userHasRecording && !isRecordingComplete -> {
                            Log.d("MAIN", "📝 Showing VoiceRecordingScreen - User needs to record")

                            // AuthViewModel'i al
                            val authViewModel: AuthViewModel = viewModel()
                            val coroutineScope = rememberCoroutineScope()

                            VoiceRecordingScreen(
                                onComplete = { audio: String?, image: Uri?, duration: Long ->
                                    audioFilePath = audio
                                    imageUri = image
                                    recordingDuration = duration

                                    Log.d("MAIN", "🎤 Recording completed:")
                                    Log.d("MAIN", "  Audio: $audio")
                                    Log.d("MAIN", "  Image: $image")
                                    Log.d("MAIN", "  Duration: $duration ms")

                                    // Backend'e yükle
                                    coroutineScope.launch {
                                        Log.d("MAIN", "📤 Uploading recording to backend...")
                                        val success = authViewModel.updateRecording(
                                            audioPath = audio,
                                            imagePath = image?.toString(),
                                            duration = duration
                                        )

                                        if (success) {
                                            Log.d("MAIN", "✅ Recording uploaded successfully!")
                                            isRecordingComplete = true
                                        } else {
                                            Log.e("MAIN", "❌ Recording upload failed!")
                                            // Yine de devam et (offline kullanım için)
                                            isRecordingComplete = true
                                        }
                                    }
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        // Recording tamamlandıysa veya kullanıcının zaten recording'i varsa
                        isAuthenticated && (userHasRecording || isRecordingComplete) && !hasStartedLearning -> {
                            Log.d("MAIN", "👤 Showing ProfileScreen")
                            ProfileScreen(
                                audioFilePath = audioFilePath,
                                imageUri = imageUri.toString(),
                                recordingDuration = recordingDuration,
                                onStartLearning = {
                                    hasStartedLearning = true
                                    Log.d("MAIN", "🎓 Starting learning...")
                                },
                                showBackButton = false,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        // Lesson açıksa
                        selectedLesson != null -> {
                            LessonScreen(
                                lesson = selectedLesson!!,
                                onComplete = {
                                    selectedLesson = null
                                    Log.d("MAIN", "✅ Lesson completed")
                                },
                                onBack = {
                                    selectedLesson = null
                                    Log.d("MAIN", "⬅️ Back from lesson")
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        // Module seçilmişse
                        selectedModule != null -> {
                            LessonListScreen(
                                module = selectedModule!!,
                                onLessonClick = { lesson ->
                                    selectedLesson = lesson
                                    Log.d("MAIN", "📖 Selected lesson: ${lesson.title}")
                                },
                                onBack = {
                                    selectedModule = null
                                    Log.d("MAIN", "⬅️ Back from module")
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        // Ana ekran
                        else -> {
                            if (showProfile) {
                                ProfileScreen(
                                    audioFilePath = audioFilePath,
                                    imageUri = imageUri.toString(),
                                    recordingDuration = recordingDuration,
                                    onStartLearning = {
                                        showProfile = false
                                        Log.d("MAIN", "⬅️ Back from profile")
                                    },
                                    showBackButton = true,
                                    modifier = Modifier.padding(innerPadding)
                                )
                            } else {
                                Log.d("MAIN", "📚 Showing ModuleListScreen (Learning)")
                                ModuleListScreen(
                                    onModuleClick = { module ->
                                        selectedModule = module
                                        Log.d("MAIN", "📂 Selected module: ${module.title}")
                                    },
                                    onProfileClick = {
                                        showProfile = true
                                        Log.d("MAIN", "👤 Opening profile")
                                    },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LessonListScreen(
    module: LearningModule,
    onLessonClick: (Lesson) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
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
                text = module.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = SouLingoColors.DeepIndigo
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${module.lessons.size} lessons available",
            fontSize = 13.sp,
            color = SouLingoColors.TextSecondary,
            modifier = Modifier.padding(start = 56.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(module.lessons) { lesson ->
                LessonCard(
                    lesson = lesson,
                    onClick = { onLessonClick(lesson) }
                )
            }
        }
    }
}

@Composable
fun LessonCard(
    lesson: Lesson,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassmorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SouLingoColors.DeepIndigo
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when (lesson.type) {
                        LessonType.LECTURE_REPETITION -> "Lecture & Repetition"
                        LessonType.QUESTION_ANSWER -> "Q&A Practice"
                    },
                    fontSize = 12.sp,
                    color = SouLingoColors.TextSecondary
                )
            }

            if (lesson.isCompleted) {
                Text(
                    text = "✓",
                    fontSize = 24.sp,
                    color = SouLingoColors.Success
                )
            } else {
                Text(
                    text = "→",
                    fontSize = 24.sp,
                    color = SouLingoColors.LuminousMint
                )
            }
        }
    }
}