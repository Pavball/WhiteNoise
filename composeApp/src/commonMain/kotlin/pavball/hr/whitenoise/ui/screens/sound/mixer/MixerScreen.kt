package pavball.hr.whitenoise.ui.screens.sound.mixer

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pavball.hr.whitenoise.domain.model.SoundMix
import pavball.hr.whitenoise.ui.components.button.DozzzePlayButton
import pavball.hr.whitenoise.ui.components.dialog.DozzzeAlertDialog
import pavball.hr.whitenoise.ui.components.dialog.SaveMixDialog
import pavball.hr.whitenoise.ui.components.effects.DozzzeGlassCard
import pavball.hr.whitenoise.ui.components.effects.WaveformVisualizer
import pavball.hr.whitenoise.ui.components.input.DozzzeVolumeSlider
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.components.utils.fromHex
import pavball.hr.whitenoise.ui.components.utils.resolveGradientColors
import pavball.hr.whitenoise.ui.components.utils.soundAccentColor
import pavball.hr.whitenoise.ui.theme.Mint
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.bg_starry
import whitenoise.composeapp.generated.resources.cancel
import whitenoise.composeapp.generated.resources.clear_layers
import whitenoise.composeapp.generated.resources.delete
import whitenoise.composeapp.generated.resources.delete_mix_confirm
import whitenoise.composeapp.generated.resources.ic_add
import whitenoise.composeapp.generated.resources.ic_delete
import whitenoise.composeapp.generated.resources.layers
import whitenoise.composeapp.generated.resources.layers_full
import whitenoise.composeapp.generated.resources.layers_hint
import whitenoise.composeapp.generated.resources.main_sound
import whitenoise.composeapp.generated.resources.mix_cosy_rain
import whitenoise.composeapp.generated.resources.mix_deep_focus
import whitenoise.composeapp.generated.resources.mix_seaside
import whitenoise.composeapp.generated.resources.mix_stormy_night
import whitenoise.composeapp.generated.resources.mixes
import whitenoise.composeapp.generated.resources.nature_sounds
import whitenoise.composeapp.generated.resources.noise_brown_hint
import whitenoise.composeapp.generated.resources.noise_colors
import whitenoise.composeapp.generated.resources.noise_pink_hint
import whitenoise.composeapp.generated.resources.noise_white_hint
import whitenoise.composeapp.generated.resources.save_mix
import whitenoise.composeapp.generated.resources.select_sound
import whitenoise.composeapp.generated.resources.sound_mixer
import whitenoise.composeapp.generated.resources.your_sounds
import kotlin.math.roundToInt

private const val NEW_LAYER_VOLUME = 0.5f

private val noiseHints: Map<String, StringResource> = mapOf(
    "white_noise" to Res.string.noise_white_hint,
    "pink_noise" to Res.string.noise_pink_hint,
    "brown_noise" to Res.string.noise_brown_hint
)

private val suggestedMixNames: Map<Long, StringResource> = mapOf(
    SoundMix.SUGGESTED_STORMY_NIGHT to Res.string.mix_stormy_night,
    SoundMix.SUGGESTED_COSY_RAIN to Res.string.mix_cosy_rain,
    SoundMix.SUGGESTED_DEEP_FOCUS to Res.string.mix_deep_focus,
    SoundMix.SUGGESTED_SEASIDE to Res.string.mix_seaside
)

/** One sound that can be layered: built-in sounds have no custom color. */
private data class MixerSound(val id: String, val label: String, val colorHex: String?)

/**
 * Layers other sounds on top of the main one, each with its own volume, and
 * saves or recalls whole mixes.
 */
@Composable
internal fun MixerScreen(viewModel: MainScreenViewModel) {
    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

    val state by viewModel.viewState<MainScreenViewState>()
        .collectAsState(initial = MainScreenViewState())
    val spectrum by viewModel.spectrum.collectAsState(initial = FloatArray(0))

    var showSaveDialog by remember { mutableStateOf(false) }
    var mixToDelete by remember { mutableStateOf<SoundMix?>(null) }

    val builtIn = state.sounds
        .filter { it.second != state.selectedSoundKey }
        .map { (label, id) -> MixerSound(id, label, null) }
    val groups = listOf(
        Res.string.noise_colors to builtIn.filter { it.id in MainScreenViewState.noiseSoundKeys },
        Res.string.nature_sounds to builtIn.filter { it.id !in MainScreenViewState.noiseSoundKeys },
        Res.string.your_sounds to state.customSounds
            .filter { it.id != state.selectedSoundKey }
            .map { MixerSound(it.id, it.displayName, it.colorId) }
    ).filter { (_, sounds) -> sounds.isNotEmpty() }
    val layersFull = state.layers.size >= SoundMix.MAX_LAYERS

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.bg_starry),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 40.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = stringResource(Res.string.sound_mixer),
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = balooFont,
                    color = Mint,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                MainSoundCard(
                    state = state,
                    spectrum = { spectrum },
                    font = quickSandFont,
                    onTogglePlay = viewModel::togglePlayPause,
                    onVolumeChange = viewModel::setLeadVolume
                )
            }

            item { SectionTitle(stringResource(Res.string.mixes), quickSandFont) }

            item {
                MixesRow(
                    state = state,
                    font = quickSandFont,
                    onApply = viewModel::applyMix,
                    onSave = { showSaveDialog = true },
                    onDelete = { mixToDelete = it }
                )
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SectionTitle(stringResource(Res.string.layers), quickSandFont, Modifier.weight(1f))
                    if (state.activeLayers.isNotEmpty()) {
                        TextButton(onClick = viewModel::clearLayers) {
                            Text(stringResource(Res.string.clear_layers), fontFamily = quickSandFont, color = Mint)
                        }
                    }
                }
                Text(
                    text = stringResource(
                        if (layersFull) Res.string.layers_full else Res.string.layers_hint,
                        SoundMix.MAX_LAYERS
                    ),
                    fontFamily = quickSandFont,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            items(groups, key = { (title, _) -> title.key }) { (title, sounds) ->
                DozzzeGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = stringResource(title).uppercase(),
                        fontFamily = quickSandFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        letterSpacing = 2.sp,
                        color = Color.White.copy(alpha = 0.55f)
                    )
                    sounds.forEachIndexed { index, sound ->
                        if (index > 0) HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                        val volume = state.layers[sound.id] ?: 0f
                        LayerRow(
                            sound = sound,
                            hint = noiseHints[sound.id]?.let { stringResource(it) },
                            volume = volume,
                            enabled = volume > 0f || !layersFull,
                            font = quickSandFont,
                            onVolumeChange = { viewModel.setLayerVolume(sound.id, it) }
                        )
                    }
                }
            }
        }
    }

    if (showSaveDialog) {
        SaveMixDialog(
            suggestedName = state.selectedSoundLabel.orEmpty(),
            onDismiss = { showSaveDialog = false },
            onSave = viewModel::saveCurrentMix
        )
    }

    mixToDelete?.let { mix ->
        DozzzeAlertDialog(
            onDismissRequest = { mixToDelete = null },
            text = { Text(stringResource(Res.string.delete_mix_confirm, mix.name)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteMix(mix.id)
                    mixToDelete = null
                }) { Text(stringResource(Res.string.delete), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { mixToDelete = null }) { Text(stringResource(Res.string.cancel)) }
            }
        )
    }
}

@Composable
private fun SectionTitle(text: String, font: FontFamily, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontFamily = font,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = Color.White,
        modifier = modifier.padding(top = 8.dp)
    )
}

@Composable
private fun MainSoundCard(
    state: MainScreenViewState,
    spectrum: () -> FloatArray,
    font: FontFamily,
    onTogglePlay: () -> Unit,
    onVolumeChange: (Float) -> Unit
) {
    val customHex = state.selectedCustomSound?.colorId
    val accent = soundAccentColor(state.selectedSoundKey, customHex)

    DozzzeGlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.main_sound).uppercase(),
                    fontFamily = font,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp,
                    color = Color.White.copy(alpha = 0.55f)
                )
                Text(
                    text = state.selectedSoundLabel ?: stringResource(Res.string.select_sound),
                    fontFamily = font,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            DozzzePlayButton(isPlaying = state.isPlaying, onClick = onTogglePlay, size = 52.dp)
        }

        SpacerHelper(12.dp)

        WaveformVisualizer(
            isPlaying = state.isPlaying,
            soundType = state.selectedSoundKey,
            customColor = customHex?.let { Color.fromHex(it) },
            levels = spectrum,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        )

        SpacerHelper(4.dp)

        Row(verticalAlignment = Alignment.CenterVertically) {
            DozzzeVolumeSlider(
                value = state.leadVolume,
                onValueChange = onVolumeChange,
                color = accent,
                modifier = Modifier.weight(1f)
            )
            PercentLabel(state.leadVolume, font)
        }
    }
}

@Composable
private fun MixesRow(
    state: MainScreenViewState,
    font: FontFamily,
    onApply: (SoundMix) -> Unit,
    onSave: () -> Unit,
    onDelete: (SoundMix) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            val shape = RoundedCornerShape(20.dp)
            Column(
                modifier = Modifier
                    .size(width = 120.dp, height = 96.dp)
                    .clip(shape)
                    .background(Color.White.copy(alpha = 0.06f))
                    .border(1.dp, Mint.copy(alpha = 0.6f), shape)
                    .clickable(onClick = onSave),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_add),
                    contentDescription = null,
                    tint = Mint,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = stringResource(Res.string.save_mix),
                    fontFamily = font,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Mint
                )
            }
        }
        items(state.savedMixes + SoundMix.suggested, key = { it.id }) { mix ->
            MixCard(
                mix = mix,
                name = suggestedMixNames[mix.id]?.let { stringResource(it) } ?: mix.name,
                state = state,
                font = font,
                onClick = { onApply(mix) },
                onDelete = if (mix.isSuggested) null else ({ onDelete(mix) })
            )
        }
    }
}

@Composable
private fun MixCard(
    mix: SoundMix,
    name: String,
    state: MainScreenViewState,
    font: FontFamily,
    onClick: () -> Unit,
    onDelete: (() -> Unit)?
) {
    val leadColorHex = state.customSounds.firstOrNull { it.id == mix.leadSoundId }?.colorId
    val colors = resolveGradientColors(mix.leadSoundId, leadColorHex)
    val isCurrent = state.selectedSoundKey == mix.leadSoundId && state.activeLayers.keys == mix.layers.keys
    val shape = RoundedCornerShape(20.dp)
    val leadLabel = state.labelFor(mix.leadSoundId) ?: "?"
    val summary = if (mix.layers.isEmpty()) leadLabel else "$leadLabel + ${mix.layers.size}"

    Box(
        modifier = Modifier
            .size(width = 150.dp, height = 96.dp)
            .clip(shape)
            .background(Brush.linearGradient(colors))
            .border(if (isCurrent) 2.dp else 0.dp, if (isCurrent) Mint else Color.Transparent, shape)
            .clickable(onClick = onClick)
    ) {
        // darken the bottom so the text stays readable on light gradients
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f))))
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                text = name,
                fontFamily = font,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = summary,
                fontFamily = font,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (onDelete != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(onClick = onDelete),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_delete),
                    contentDescription = stringResource(Res.string.delete),
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun LayerRow(
    sound: MixerSound,
    hint: String?,
    volume: Float,
    enabled: Boolean,
    font: FontFamily,
    onVolumeChange: (Float) -> Unit
) {
    val isOn = volume > 0f
    val accent = soundAccentColor(sound.id, sound.colorHex)

    Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
        // Tapping the name switches the layer on at a medium volume, or off.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(enabled = enabled) { onVolumeChange(if (isOn) 0f else NEW_LAYER_VOLUME) }
                .padding(vertical = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (isOn) accent else accent.copy(alpha = 0.35f))
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sound.label,
                    fontFamily = font,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = if (isOn) 1f else 0.75f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                hint?.let {
                    Text(text = it, fontFamily = font, fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f))
                }
            }
            if (isOn) PercentLabel(volume, font)
        }
        DozzzeVolumeSlider(
            value = volume,
            onValueChange = onVolumeChange,
            color = accent,
            enabled = enabled
        )
    }
}

@Composable
private fun PercentLabel(volume: Float, font: FontFamily) {
    Text(
        text = "${(volume * 100).roundToInt()}%",
        fontFamily = font,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        color = Color.White.copy(alpha = 0.8f),
        textAlign = TextAlign.End,
        modifier = Modifier.width(44.dp)
    )
}
