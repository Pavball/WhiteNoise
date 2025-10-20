package pavball.hr.whitenoise.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.jetbrains.compose.resources.painterResource
import pavball.hr.whitenoise.ui.components.MediaPlayerComponent
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.ic_pause
import whitenoise.composeapp.generated.resources.ic_play
import whitenoise.composeapp.generated.resources.ic_stop

@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {
    var start by remember { mutableStateOf(false) }
    var pause by remember { mutableStateOf(false) }
    var stop by remember { mutableStateOf(false) }

    val whiteNoiseOptions = listOf(
        "Sound Helix" to  "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Paza" to "http://codeskulptor-demos.commondatastorage.googleapis.com/pang/paza-moduless.mp3",
        "Background Music" to "http://commondatastorage.googleapis.com/codeskulptor-assets/sounddogs/thrust.mp3",
        "Thrust" to "http://codeskulptor-demos.commondatastorage.googleapis.com/descent/background%20music.mp3"
    )

    var selectedNoise by remember { mutableStateOf(whiteNoiseOptions[0]) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var expanded by remember { mutableStateOf(false) }

        Button(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp),
            onClick = { expanded = !expanded },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue.copy(alpha = 0.5f))
        ) {
            Text(
                text = selectedNoise.first,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { expanded = true }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                whiteNoiseOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.first) },
                        onClick = {
                            selectedNoise = option
                            expanded = false
                            stop = true // stop previous playback when switching
                        }
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(
                onClick = {
                    start = true
                    pause = false
                    stop = false
                },
                modifier = modifier.background(Color.Transparent),
                enabled = true,
            ) {
                Icon(
                    painter = painterResource(
                        resource = Res.drawable.ic_play
                    ),
                    contentDescription = "Play",
                    tint = Color.Blue.copy(alpha = 1f),
                    modifier = modifier.size(24.dp)
                )
            }

            IconButton(
                onClick = {
                    pause = true
                    start = false
                    stop = false
                },
                modifier = modifier.background(Color.Transparent),
                enabled = true,
            ) {
                Icon(
                    painter = painterResource(
                        resource = Res.drawable.ic_pause
                    ),
                    contentDescription = "Pause",
                    tint = Color.Blue.copy(alpha = 1f),
                    modifier = modifier.size(24.dp)
                )
            }

            IconButton(
                onClick = {
                    stop = true
                    start = false
                    pause = false
                },
                modifier = modifier.background(Color.Transparent),
                enabled = true,
            ) {
                Icon(
                    painter = painterResource(
                        resource = Res.drawable.ic_stop
                    ),
                    contentDescription = "Stop",
                    tint = Color.Blue.copy(alpha = 1f),
                    modifier = modifier.size(24.dp)
                )
            }
        }

        MediaPlayerComponent(
            modifier = Modifier.fillMaxWidth(),
            url = selectedNoise.second,
            start = start,
            pause = pause,
            stop = stop
        )
    }
}