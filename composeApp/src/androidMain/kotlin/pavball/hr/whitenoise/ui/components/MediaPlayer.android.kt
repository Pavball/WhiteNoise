package pavball.hr.whitenoise.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import pavball.hr.whitenoise.R // access to res/raw
import kotlinx.coroutines.delay
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
actual fun MediaPlayerComponent(
    modifier: Modifier,
    resId: String,
    start: Boolean,
    pause: Boolean,
    stop: Boolean,
    isLoading: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    var duration by remember { mutableStateOf(0L) }
    var currentPosition by remember { mutableStateOf(0L) }

//  Map resource keys to actual res/raw IDs
    val resourceMap = mapOf(
        "rain" to R.raw.rain,
        "ocean" to R.raw.ocean_waves,
        "forest" to R.raw.winter_forest
    )

    val resolvedResId = resourceMap[resId] ?: return

// --- Load sound when changed ---
    LaunchedEffect(resolvedResId) {
        isLoading(true)

        exoPlayer.stop()
        exoPlayer.clearMediaItems()

        val uri = "android.resource://${context.packageName}/$resolvedResId".toUri()
        val mediaItem = MediaItem.fromUri(uri)

        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_ONE // optional looping

        isLoading(false)

        delay(300)
        duration = exoPlayer.duration.takeIf { it > 0L } ?: 1L
    }

// --- Playback handling ---
    LaunchedEffect(start, pause, stop) {
        when {
            stop -> {
                exoPlayer.pause()
                exoPlayer.seekTo(0)
            }
            start -> exoPlayer.play()
            pause -> exoPlayer.pause()
        }
    }

// --- Track progress ---
    LaunchedEffect(start, pause) {
        while (start && !pause && exoPlayer.isPlaying) {
            currentPosition = exoPlayer.currentPosition
            delay(500)
        }
    }

// --- UI ---
    if (duration > 1) {
        Column(modifier = modifier.padding(16.dp)) {
            Slider(
                value = currentPosition.toFloat(),
                onValueChange = { currentPosition = it.toLong() },
                onValueChangeFinished = {
                    exoPlayer.seekTo(currentPosition)
                },
                valueRange = 0f..duration.toFloat()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatTime(currentPosition))
                Text(formatTime(duration))
            }
        }
    }

    SleepTimerControl(
        onFadeStart = {
            fadeOutVolume(exoPlayer, coroutineScope)
        },
        onTimerFinished = {
            exoPlayer.stop()
            exoPlayer.volume = 1f
        },
        isPlaying = exoPlayer.isPlaying
    )

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}

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