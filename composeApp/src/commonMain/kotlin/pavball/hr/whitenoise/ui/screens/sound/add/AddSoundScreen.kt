package pavball.hr.whitenoise.ui.screens.sound.add

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pavball.hr.whitenoise.domain.model.CustomSound
import pavball.hr.whitenoise.domain.model.rememberSoundPicker
import pavball.hr.whitenoise.ui.components.button.DozzzeButton
import pavball.hr.whitenoise.ui.components.dialog.AddCustomSoundDialog
import pavball.hr.whitenoise.ui.components.effects.drawVerticalFadingEdges
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.components.utils.resolveGradientColors
import pavball.hr.whitenoise.ui.theme.LightGray
import pavball.hr.whitenoise.ui.theme.Mint
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.add_sound
import whitenoise.composeapp.generated.resources.add_sound_hint
import whitenoise.composeapp.generated.resources.are_you_sure
import whitenoise.composeapp.generated.resources.bg_blue
import whitenoise.composeapp.generated.resources.cancel
import whitenoise.composeapp.generated.resources.choose_audio_file
import whitenoise.composeapp.generated.resources.delete
import whitenoise.composeapp.generated.resources.edit
import whitenoise.composeapp.generated.resources.ic_delete
import whitenoise.composeapp.generated.resources.ic_edit
import whitenoise.composeapp.generated.resources.no_custom_sounds
import whitenoise.composeapp.generated.resources.your_sounds

@Composable
internal fun AddSoundScreen(modifier: Modifier = Modifier, viewModel: MainScreenViewModel) {

    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

    val state by viewModel.viewState<MainScreenViewState>()
        .collectAsState(initial = MainScreenViewState())
    val pendingRename by viewModel.pendingRename.collectAsState()

    var editingSound by remember { mutableStateOf<CustomSound?>(null) }
    var deletingSound by remember { mutableStateOf<CustomSound?>(null) }

    val listState = rememberLazyListState()

    // Sound Picker Launcher
    val launchSoundPicker = rememberSoundPicker { picked ->
        if (picked != null) {
            viewModel.addUserSound(picked.displayName, picked.uri)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.bg_blue),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
        )

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .drawVerticalFadingEdges(listState),
            state = listState,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 48.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(Res.string.add_sound),
                        style = MaterialTheme.typography.headlineLarge,
                        fontFamily = balooFont,
                        fontWeight = FontWeight.Normal,
                        color = Mint
                    )

                    SpacerHelper(16.dp)

                    Text(
                        text = stringResource(Res.string.add_sound_hint),
                        fontFamily = quickSandFont,
                        color = LightGray.copy(alpha = 0.8f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    SpacerHelper(28.dp)

                    DozzzeButton(
                        onClicked = { launchSoundPicker() },
                        buttonText = stringResource(Res.string.choose_audio_file),
                        buttonWidth = 250.dp,
                        buttonTextStyle = MaterialTheme.typography.bodyMedium,
                        buttonTextFont = quickSandFont,
                        buttonTextWeight = FontWeight.Bold,
                        shape = RoundedCornerShape(20.dp)
                    )

                    SpacerHelper(36.dp)

                    Text(
                        text = stringResource(Res.string.your_sounds),
                        fontFamily = quickSandFont,
                        fontWeight = FontWeight.Bold,
                        color = Mint,
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (state.customSounds.isEmpty()) {
                item {
                    Text(
                        text = stringResource(Res.string.no_custom_sounds),
                        fontFamily = quickSandFont,
                        color = LightGray.copy(alpha = 0.7f),
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }
            }

            items(state.customSounds, key = { it.id }) { sound ->
                CustomSoundRow(
                    sound = sound,
                    fontFamily = quickSandFont,
                    onPlay = { viewModel.playSound(sound.id) },
                    onEdit = { editingSound = sound },
                    onDelete = { deletingSound = sound }
                )
            }
        }
    }

    // --- Name + color dialog for a just-added sound, or one being edited ---
    (editingSound ?: pendingRename)?.let { cs -> key(cs.id) {
        val controller = rememberColorPickerController()

        AddCustomSoundDialog(
            currentName = cs.displayName,
            currentColorHex = cs.colorId,
            onDismiss = {
                editingSound = null
                viewModel.clearPendingRename()
            },
            onRename = { newName ->
                viewModel.renameCustomSound(id = cs.id, newName = newName)
            },
            onColorChange = { colorId ->
                viewModel.updateCustomSoundColor(id = cs.id, colorId = colorId)
            },
            controller = controller
        )
    } }

    // --- Delete confirmation ---
    deletingSound?.let { sound ->
        AlertDialog(
            onDismissRequest = { deletingSound = null },
            title = { Text(stringResource(Res.string.delete)) },
            text = { Text("${stringResource(Res.string.are_you_sure)} \"${sound.displayName}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.removeUserSound(sound.id)
                        deletingSound = null
                    }
                ) { Text(stringResource(Res.string.delete), color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { deletingSound = null }) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun CustomSoundRow(
    sound: CustomSound,
    fontFamily: FontFamily,
    onPlay: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .border(BorderStroke(1.dp, Mint), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable { onPlay() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(resolveGradientColors(sound.id, sound.colorId)))
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = sound.displayName,
            color = Color.White,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onEdit) {
            Icon(
                painter = painterResource(Res.drawable.ic_edit),
                contentDescription = stringResource(Res.string.edit),
                tint = LightGray,
                modifier = Modifier.size(22.dp)
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                painter = painterResource(Res.drawable.ic_delete),
                contentDescription = stringResource(Res.string.delete),
                tint = Color(0xFFFF8A80),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
