package pavball.hr.whitenoise.ui.components

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    stop: Boolean,
    onLoadingChanged: (Boolean) -> Unit
) {
    var isPrepared by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var duration by remember { mutableStateOf(0) }
    var currentPosition by remember { mutableStateOf(0) }

// Notify parent when loading changes
    LaunchedEffect(isLoading) {
        onLoadingChanged(isLoading)
    }

    println("MEDIA PLAYER COMPONENT - PREPARED: $isPrepared")
    println("MEDIA PLAYER COMPONENT - LOADING: $isLoading")
    println("MEDIA PLAYER COMPONENT - LINK: $url")


    val mediaPlayer = remember(url, stop) {
        println("MEDIA PLAYER COMPONENT - MEDIA PLAYER SET UP")
        MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }
    }

    LaunchedEffect(url, stop) {
        println("MEDIA PLAYER COMPONENT - LAUNCHED EFFECT FOR SET UP")
        isPrepared = false
        isLoading = true

        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                isPrepared = true
                isLoading = false
                duration = mediaPlayer.duration
            }
        } catch (e: IOException) {
            e.printStackTrace()
            isLoading = false
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            isLoading = false
        }
    }


    LaunchedEffect(start, pause, isPrepared) {
        println("MEDIA PLAYER COMPONENT - LAUNCHED EFFECT FOR START/PAUSE")

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

    LaunchedEffect(start, pause, isPrepared) {
        println("MEDIA PLAYER COMPONENT - LAUNCHED EFFECT FOR PROGRESS BAR")
        if (isPrepared && start && !pause) {
            while (true) {
                currentPosition = mediaPlayer.currentPosition
                delay(500)
            }
        }
    }

// 🎨 UI
    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(8.dp))
                    Text("Loading...")
                }
            }

            isPrepared && duration > 0 -> {
                Column {
                    Slider(
                        value = currentPosition.toFloat(),
                        onValueChange = { currentPosition = it.toInt() },
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
        }
    }

// Cleanup when composable leaves
    DisposableEffect(url) {
        println("MEDIA PLAYER COMPONENT - DISPOSABLE EFFECT FOR DISPOSING")
        onDispose {
            mediaPlayer.release()
        }
    }
}

// Format ms into mm:ss
fun formatTime(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

