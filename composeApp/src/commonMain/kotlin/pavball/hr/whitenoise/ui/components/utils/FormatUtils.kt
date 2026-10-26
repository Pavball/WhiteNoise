package pavball.hr.whitenoise.ui.components.utils

/** Formats a duration as "mm:ss", or "h:mm:ss" from one hour up. */
fun formatTime(ms: Long): String {
    val totalSeconds = (ms.coerceAtLeast(0) + 999) / 1000 // round up so 0:00 means done
    val hours = totalSeconds / 3600
    val minutes = ((totalSeconds % 3600) / 60).toString().padStart(2, '0')
    val seconds = (totalSeconds % 60).toString().padStart(2, '0')
    return if (hours > 0) "$hours:$minutes:$seconds" else "$minutes:$seconds"
}

/** Formats a time of day as "HH:mm". */
fun formatClock(hour: Int, minute: Int): String =
    "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
