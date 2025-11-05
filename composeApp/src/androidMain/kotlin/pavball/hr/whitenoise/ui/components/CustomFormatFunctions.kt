package pavball.hr.whitenoise.ui.components

import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

fun fadeOutVolume(player: ExoPlayer, scope: CoroutineScope) {
    scope.launch {
        val fadeDuration = 30_000L
        val fadeSteps = 30
        val delayPerStep = fadeDuration / fadeSteps

        for (i in 0..fadeSteps) {
            val newVolume = 1f - (i / fadeSteps.toFloat())
            player.volume = newVolume.coerceAtLeast(0f)
            delay(delayPerStep)
        }

        player.volume = 0f
        player.pause()
    }
}