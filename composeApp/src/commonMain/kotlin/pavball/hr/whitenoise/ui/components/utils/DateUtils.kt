package pavball.hr.whitenoise.ui.components.utils

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun nowEpochMillis(): Long = Clock.System.now().toEpochMilliseconds()

@OptIn(ExperimentalTime::class)
fun Long.toLocalDateTime(): LocalDateTime =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault())

/** "14.01.2026." */
fun formatDate(epochMillis: Long): String {
    val date = epochMillis.toLocalDateTime().date
    return "${date.day.pad()}.${date.month.number.pad()}.${date.year}."
}

/** "22:46" */
fun formatClockTime(epochMillis: Long): String {
    val time = epochMillis.toLocalDateTime()
    return formatClock(time.hour, time.minute)
}

/** "7h 45m", or "45m" under an hour. */
fun formatDuration(ms: Long): String {
    val totalMinutes = ms.coerceAtLeast(0) / 60_000
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
}

/** The next time the clock shows [hour]:[minute], strictly after [nowMillis]. */
@OptIn(ExperimentalTime::class)
fun nextOccurrenceOf(hour: Int, minute: Int, nowMillis: Long): Long {
    val zone = TimeZone.currentSystemDefault()
    val now = nowMillis.toLocalDateTime()
    var candidate = LocalDateTime(now.date, LocalTime(hour, minute)).toInstant(zone).toEpochMilliseconds()
    if (candidate <= nowMillis) {
        val tomorrow = now.date.plus(1, DateTimeUnit.DAY)
        candidate = LocalDateTime(tomorrow, LocalTime(hour, minute)).toInstant(zone).toEpochMilliseconds()
    }
    return candidate
}

fun LocalDate.plusDays(days: Int): LocalDate = plus(days, DateTimeUnit.DAY)

/** "MON", "TUE", … */
fun DayOfWeek.shortLabel(): String = name.take(3)

private fun Int.pad() = toString().padStart(2, '0')
