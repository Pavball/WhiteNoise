package pavball.hr.whitenoise.ui.components

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import okio.IOException

@Composable
actual fun MediaPlayerComponent(
    modifier: Modifier,
    url: String,
    start: Boolean,
    pause: Boolean,
    stop: Boolean
) {
    val mediaPlayer = remember { MediaPlayer() }
    var isPrepared by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf(0) }
    var currentPosition by remember { mutableStateOf(0) }

// Init / Re-init when URL changes or stop is triggered
    LaunchedEffect(url, stop) {
        if (stop) {
            try {
                mediaPlayer.stop()
            } catch (_: IllegalStateException) {}

            mediaPlayer.reset()
            isPrepared = false
        }

        if (!isPrepared) {
            try {
                mediaPlayer.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                mediaPlayer.setDataSource(url)
                mediaPlayer.prepareAsync()
                mediaPlayer.setOnPreparedListener {
                    isPrepared = true
                    duration = mediaPlayer.duration
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

// React to start/pause
    LaunchedEffect(start, pause, isPrepared) {
        if (!isPrepared) return@LaunchedEffect

        when {
            start && !pause -> {
                if (!mediaPlayer.isPlaying) mediaPlayer.start()
            }
            pause -> {
                if (mediaPlayer.isPlaying) mediaPlayer.pause()
            }
        }
    }

// Update progress
    LaunchedEffect(start, pause, isPrepared) {
        if (isPrepared && start && !pause) {
            while (true) {
                currentPosition = mediaPlayer.currentPosition
                delay(500)
            }
        }
    }

// Slider UI
    if (isPrepared && duration > 0) {
        Column(modifier = modifier.padding(16.dp)) {
            Slider(
                value = currentPosition.toFloat(),
                onValueChange = { newValue ->
                    currentPosition = newValue.toInt()
                },
                onValueChangeFinished = {
                    mediaPlayer.seekTo(currentPosition)
                },
                valueRange = 0f..duration.toFloat(),
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

// Dispose only when screen leaves
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }
}

// Helper function to format ms into mm:ss
fun formatTime(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}