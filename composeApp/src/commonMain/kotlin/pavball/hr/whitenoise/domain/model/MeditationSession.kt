package pavball.hr.whitenoise.domain.model

import pavball.hr.whitenoise.db.Meditation_session

enum class Mood { ANXIETY, SAD, HAPPY }

data class MeditationSession(
    val id: Long,
    val mood: Mood,
    val completedAt: Long, // epoch millis
    val durationMs: Long
)

internal fun Meditation_session.toMeditationSession() =
    MeditationSession(
        id = id,
        mood = Mood.entries.firstOrNull { it.name == mood } ?: Mood.HAPPY,
        completedAt = completed_at,
        durationMs = duration_ms
    )
