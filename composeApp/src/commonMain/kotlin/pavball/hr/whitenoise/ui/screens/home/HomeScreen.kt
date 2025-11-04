package pavball.hr.whitenoise.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

// Get isLoading state from MediaPlayerComponent
    var isLoading by remember { mutableStateOf(true) }

    val sounds = listOf(
        "Rain" to "rain",
        "Ocean Waves" to "ocean",
        "Forest Ambience" to "forest"
    )

    var selectedSound by remember { mutableStateOf(sounds.first()) }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            onClick = { expanded = !expanded },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue.copy(alpha = 0.5f))
        ) {
            Text(selectedSound.first)
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                sounds.forEach { (label, resId) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            expanded = false
                            selectedSound = label to resId
                            // Auto-play new sound
                            start = true
                            pause = false
                            stop = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(
                onClick = {
                    start = true
                    pause = false
                    stop = false
                },
                enabled = !isLoading
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_play),
                    contentDescription = "Play",
                    tint = if (isLoading) Color.Gray else Color.Blue
                )
            }

            IconButton(
                onClick = {
                    pause = true
                    start = false
                    stop = false
                },
                enabled = !isLoading
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_pause),
                    contentDescription = "Pause",
                    tint = if (isLoading) Color.Gray else Color.Blue
                )
            }

            IconButton(
                onClick = {
                    stop = true
                    start = false
                    pause = false
                },
                enabled = !isLoading
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_stop),
                    contentDescription = "Stop",
                    tint = if (isLoading) Color.Gray else Color.Blue
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        MediaPlayerComponent(
            modifier = Modifier.fillMaxWidth(),
            resId = selectedSound.second,
            start = start,
            pause = pause,
            stop = stop,
            isLoading = {
                isLoading = it
            }
        )

    }
}

