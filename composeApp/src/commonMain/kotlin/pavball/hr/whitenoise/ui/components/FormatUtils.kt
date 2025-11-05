package pavball.hr.whitenoise.ui.components

fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000)
    val minutes = (totalSeconds / 60).toString().padStart(2, '0')
    val seconds = (totalSeconds % 60).toString().padStart(2, '0')
    return "$minutes:$seconds"
}
