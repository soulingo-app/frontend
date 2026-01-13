package com.soulingo.app.data.model

data class LearningModule(
    val id: String,
    val level: String,
    val title: String,
    val lessons: List<Lesson>,
    val isLocked: Boolean = false,
    val progress: Int = 0
)

enum class LessonType {
    LECTURE_REPETITION,
    QUESTION_ANSWER
}

data class PronunciationResult(
    val score: Int,
    val mistakes: List<PronunciationMistake>,
    val feedback: String
)

data class PronunciationMistake(
    val word: String,
    val expectedPronunciation: String,
    val actualPronunciation: String,
    val timestamp: Long
)