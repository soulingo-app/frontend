package com.soulingo.app.data.model

object SpanishContent {
    val modules = listOf(
        LearningModule(
            id = "a1",
            level = "A1",
            title = "Basic Level",
            progress = 0,
            isLocked = false,
            lessons = listOf(
                Lesson(
                    id = 1,
                    lessonId = "a1_l1",
                    title = "Introduction & Daily Routine",
                    content = """
                        Hola. Me llamo Lucy. Soy estudiante. Estudio todos los días. Soy de Turquía. Me levanto temprano. Bebo agua. Voy a la escuela. Vuelvo a casa. Estudio español.
                    """.trimIndent(),
                    level = "A1",
                    lessonType = "lecture_repetition",
                    createdAt = "",
                    updatedAt = "",
                    videoUrl = null,
                    audioUrl = null,
                    isCompleted = false
                ),
                Lesson(
                    id = 2,
                    lessonId = "a1_l2",
                    title = "Simple Questions",
                    content = """
                        Q: ¿Cuántos dedos tienes?
                        A: Diez.
                        
                        Q: ¿De qué color es la leche?
                        A: Blanca.
                        
                        Q: ¿Qué mes viene después de enero?
                        A: Febrero.
                    """.trimIndent(),
                    level = "A1",
                    lessonType = "question_answer",
                    createdAt = "",
                    updatedAt = "",
                    videoUrl = null,
                    audioUrl = null,
                    isCompleted = false
                )
            )
        ),
        LearningModule(
            id = "a2",
            level = "A2",
            title = "Elementary Level",
            progress = 0,
            isLocked = true,
            lessons = listOf(
                Lesson(
                    id = 3,
                    lessonId = "a2_l1",
                    title = "Past Tense & Routines",
                    content = """
                        Ayer estudié español. Vi un video corto. Fue útil.
                        
                        Hoy conocí a una persona nueva. Ella fue muy amable. Hablamos un rato.
                        
                        Vivo en una casa pequeña pero cómoda. Hay dos habitaciones, una cocina y un baño.
                        
                        Mi habitación es luminosa y tranquila. Me gusta pasar tiempo allí.
                    """.trimIndent(),
                    level = "A2",
                    lessonType = "lecture_repetition",
                    createdAt = "",
                    updatedAt = "",
                    videoUrl = null,
                    audioUrl = null,
                    isCompleted = false
                )
            )
        ),
        LearningModule(
            id = "b1",
            level = "B1",
            title = "Intermediate Level",
            progress = 0,
            isLocked = true,
            lessons = listOf(
                Lesson(
                    id = 4,
                    lessonId = "b1_l1",
                    title = "Future Plans & Complex Sentences",
                    content = """
                        Normalmente me despierto temprano entre semana y empiezo el día con una caminata corta.
                        
                        En el futuro, quiero trabajar en un campo relacionado con la tecnología.
                        
                        Cuando empecé a aprender español, me sentía tímido e inseguro al hablar.
                    """.trimIndent(),
                    level = "B1",
                    lessonType = "lecture_repetition",
                    createdAt = "",
                    updatedAt = "",
                    videoUrl = null,
                    audioUrl = null,
                    isCompleted = false
                )
            )
        )
    )
}