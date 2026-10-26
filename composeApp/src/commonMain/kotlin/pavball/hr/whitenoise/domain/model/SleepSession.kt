package pavball.hr.whitenoise.domain.model

import pavball.hr.whitenoise.db.SelectCompleted
import pavball.hr.whitenoise.db.Sleep_session

/** One tracked night. [endedAt] is null while the user is still asleep. */
data class SleepSession(
    val id: Long,
    val startedAt: Long,   // epoch millis
    val endedAt: Long?,
    val alarmAt: Long?,
    val quality: Int?      // 1..5, asked on wake-up
) {
    val durationMs: Long? get() = endedAt?.let { (it - startedAt).coerceAtLeast(0) }
}

internal fun Sleep_session.toSleepSession() =
    SleepSession(
        id = id,
        startedAt = started_at,
        endedAt = ended_at,
        alarmAt = alarm_at,
        quality = quality?.toInt()
    )

internal fun SelectCompleted.toSleepSession() =
    SleepSession(
        id = id,
        startedAt = started_at,
        endedAt = ended_at,
        alarmAt = alarm_at,
        quality = quality?.toInt()
    )
