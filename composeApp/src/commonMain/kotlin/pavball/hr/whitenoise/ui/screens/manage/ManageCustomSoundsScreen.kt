package pavball.hr.whitenoise.ui.screens.manage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pavball.hr.whitenoise.domain.model.CustomSound
import pavball.hr.whitenoise.ui.components.drawFadingEdges
import pavball.hr.whitenoise.ui.screens.home.fromHex
import pavball.hr.whitenoise.ui.screens.home.lighten
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.are_you_sure
import whitenoise.composeapp.generated.resources.cancel
import whitenoise.composeapp.generated.resources.choose_color_bg
import whitenoise.composeapp.generated.resources.delete
import whitenoise.composeapp.generated.resources.edit_sound_name
import whitenoise.composeapp.generated.resources.ic_delete
import whitenoise.composeapp.generated.resources.manage_custom_sound
import whitenoise.composeapp.generated.resources.no_custom_sounds
import whitenoise.composeapp.generated.resources.save
import whitenoise.composeapp.generated.resources.select_sound
import whitenoise.composeapp.generated.resources.sound_name

@Composable
fun ManageCustomSoundsScreen(
    customSounds: List<CustomSound>,
    onRename: (CustomSound, String) -> Unit,
    onDelete: (CustomSound) -> Unit,
    onColorChange: (CustomSound, String) -> Unit
) {
    var editingSound by remember { mutableStateOf<CustomSound?>(null) }
    var editingName by remember { mutableStateOf("") }

    // delete confirmation
    var deletingSound by remember { mutableStateOf<CustomSound?>(null) }

    val scrollableState = rememberLazyListState()
    // -------------------- Rename + Color Dialog --------------------
    editingSound?.let { sound ->
        val controller = remember { ColorPickerController() }

        // Initial color
        val initialColor = remember(sound.colorId) {
            sound.colorId.let { Color.fromHex(it) }
        }

        // Local mutable color
        var pickedColor by remember { mutableStateOf(initialColor) }
        var pickedColorHex by remember { mutableStateOf(sound.colorId) }

        AlertDialog(
            onDismissRequest = { editingSound = null },
            title = { Text(stringResource(Res.string.edit_sound_name)) },
            text = {
                Column(Modifier.fillMaxWidth()) {

                    OutlinedTextField(
                        value = editingName,
                        onValueChange = { editingName = it },
                        label = { Text(stringResource(Res.string.sound_name)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(20.dp))

                    // Gradient preview
                    val gradient = listOf(pickedColor, pickedColor.lighten(0.35f))

                    Text(stringResource(Res.string.choose_color_bg), style = MaterialTheme.typography.titleLarge)

                    Spacer(Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.linearGradient(gradient))
                    )

                    Spacer(Modifier.height(16.dp))

                    // Color picker
                    HsvColorPicker(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        controller = controller,
                        onColorChanged = { envelope: ColorEnvelope ->
                            pickedColorHex = envelope.hexCode
                            pickedColor = Color.fromHex(envelope.hexCode)
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newName = editingName.trim()
                        if (newName.isNotEmpty()) {
                            onRename(sound, newName)
                            onColorChange(sound, pickedColorHex)
                        }
                        editingSound = null
                    }
                ) { Text(stringResource(Res.string.save)) }
            },
            dismissButton = {
                TextButton(onClick = { editingSound = null }) { Text(stringResource(Res.string.cancel)) }
            }
        )
    }

    // -------------------- Delete confirmation dialog --------------------
    deletingSound?.let { sound ->
        AlertDialog(
            onDismissRequest = { deletingSound = null },
            title = { Text(stringResource(Res.string.delete)) },
            text = { Text("${stringResource(Res.string.are_you_sure)} \"${sound.displayName}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(sound)
                        deletingSound = null
                    }
                ) { Text(stringResource(Res.string.delete), color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { deletingSound = null }) { Text(stringResource(Res.string.cancel)) }
            }
        )
    }

    // -------------------- List UI --------------------
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            stringResource(Res.string.manage_custom_sound),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        if (customSounds.isEmpty()) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(Res.string.no_custom_sounds))
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .drawFadingEdges(scrollableState),
                state = scrollableState
            ) {
                items(customSounds) { sound ->
                    Surface(
                        tonalElevation = 3.dp,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                editingSound = sound
                                editingName = sound.displayName
                            }
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(sound.displayName, fontWeight = FontWeight.Medium)
                            }

                            Row {
                                IconButton(onClick = {
                                    deletingSound = sound
                                }) {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_delete),
                                        contentDescription = stringResource(Res.string.delete),
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
