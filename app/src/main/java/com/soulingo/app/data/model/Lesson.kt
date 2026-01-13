package com.soulingo.app.data.model

import com.google.gson.annotations.SerializedName

data class Lesson(
    val id: Int,
    @SerializedName("lesson_id")
    val lessonId: String,
    val title: String,
    val content: String,
    val level: String,
    @SerializedName("lesson_type")
    val lessonType: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    val videoUrl: String? = null,
    val audioUrl: String? = null,
    val isCompleted: Boolean = false
) {
    val type: LessonType
        get() = when (lessonType) {
            "lecture_repetition" -> LessonType.LECTURE_REPETITION
            "question_answer" -> LessonType.QUESTION_ANSWER
            else -> LessonType.LECTURE_REPETITION
        }
}