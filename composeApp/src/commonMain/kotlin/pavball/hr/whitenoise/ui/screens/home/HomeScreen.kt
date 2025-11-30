package pavball.hr.whitenoise.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import pavball.hr.whitenoise.domain.model.rememberSoundPicker
import pavball.hr.whitenoise.ui.components.SpacerHelper
import pavball.hr.whitenoise.ui.components.dialog.AddCustomSoundDialog
import pavball.hr.whitenoise.ui.components.formatTime
import pavball.hr.whitenoise.ui.components.sections.home.ControlsSection
import pavball.hr.whitenoise.ui.components.sections.home.FadeCheckboxSection
import pavball.hr.whitenoise.ui.components.sections.home.NowPlayingCardSection
import pavball.hr.whitenoise.ui.components.sections.home.SoundSelectorSection
import pavball.hr.whitenoise.ui.components.sections.home.TimerSection
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel
) {
    val state by viewModel.viewState<MainScreenViewState>()
        .collectAsState(initial = MainScreenViewState())

    var expanded by rememberSaveable { mutableStateOf(false) }
    val customSounds by viewModel.userSounds.collectAsState()

    val combinedSounds = remember(state.sounds, customSounds) {
        state.sounds + customSounds.map { it.displayName to it.id }
    }

    val selectedSoundLabel =
        combinedSounds.firstOrNull { it.second == state.currentSound }?.first ?: "Select sound"
    val pendingRename by viewModel.pendingRename.collectAsState()

    val launchSoundPicker = rememberSoundPicker { picked ->
        if (picked != null) {
            // add saved sound and show rename dialog
            viewModel.addUserSound(picked.displayName, picked.uri, picked.colorId)
        }
    }

    val progress = remember(state.remainingTime, state.totalTime) {
        if (state.totalTime != null && state.remainingTime != null && state.totalTime!! > 0)
            1f - (state.remainingTime!!.toFloat() / state.totalTime!!.toFloat())
        else 0f
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            NowPlayingCardSection(
                soundLabel = if (!state.isCleared) selectedSoundLabel else "Select sound",
                soundKey = state.currentSound,
                isPlaying = state.isPlaying,
                subLabel = if (state.remainingTime != null)
                    "Stopping in ${formatTime(state.remainingTime!!)}"
                else null,
                listOfColors = resolveGradientColors(
                    soundKey = state.selectedSoundKey,
                    dbColorId = viewModel.currentSoundColorId
                )
            )
        }

        item {
            // Buttons, dropdowns, etc.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SoundSelectorSection(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    combinedSounds = combinedSounds,
                    selectedSoundLabel = selectedSoundLabel,
                    launchSoundPicker = launchSoundPicker,
                    viewModel = viewModel,
                    state = state
                )
            }
        }

        item {
            // Play / Pause / Stop
            ControlsSection(
                state = state,
                progress = progress,
                viewModel = viewModel,
            )
        }

        item { SpacerHelper(8.dp) }

        item {
            TimerSection(state = state, viewModel = viewModel)
        }

        item { SpacerHelper(12.dp) }

        item {
            FadeCheckboxSection(state = state, viewModel = viewModel, time = "")
        }
    }

// --------------------------------------------------------------------
// ||  Show rename dialog when pendingRename is set by the ViewModel  ||
// --------------------------------------------------------------------

    pendingRename?.let { cs ->
        val controller = rememberColorPickerController()

        AddCustomSoundDialog(
            currentName = cs.displayName,
            currentColorHex = cs.colorId,
            onDismiss = { viewModel.clearPendingRename() },
            onRename = { newName ->
                viewModel.renameCustomSound(id = cs.id, newName = newName)
            },
            onColorChange = { colorId ->
                viewModel.updateCustomSoundColor(id = cs.id, colorId = colorId)
            },
            controller = controller
        )
    }
}

// --------------------------------------------------------------------
// ||                HELPER FUNCTIONS SECTION                        ||
// --------------------------------------------------------------------

fun resolveGradientColors(
    soundKey: String?,
    dbColorId: String?
): List<Color> {
    // If database color exists → generate gradient from that color
    if (dbColorId != null) {
        val baseColor = dbColorId.let { Color.fromHex(it) }
        return listOf(baseColor, baseColor.lighten(0.35f))
    }

    // Fallback for built-in sounds
    return when (soundKey) {
        "ocean" -> listOf(Color(0xFF2196F3), Color(0xFF64B5F6))
        "forest" -> listOf(Color(0xFF4CAF50), Color(0xFF81C784))

        // Add more built-ins here if needed
        // "rain" -> listOf(...)

        else -> listOf(
            Color(0xFF3F51B5),
            Color(0xFF7986CB)
        )
    }
}

fun Color.Companion.fromHex(hex: String?): Color {
    if (hex.isNullOrBlank()) {
        return Color(0xFF9E9E9E) // fallback
    }

    return try {
        val cleanHex = hex
            .removePrefix("#")
            .removePrefix("0x")
            .trim()

        val colorLong = cleanHex.toLong(16)

        Color(
            red = ((colorLong shr 16) and 0xFF) / 255f,
            green = ((colorLong shr 8) and 0xFF) / 255f,
            blue = (colorLong and 0xFF) / 255f,
            alpha = 1f
        )
    } catch (_: Exception) {
        Color(0xFF9E9E9E) // fallback if invalid hex
    }
}


fun Color.lighten(factor: Float = 0.3f): Color {
    return Color(
        red = (this.red + (1f - this.red) * factor),
        green = (this.green + (1f - this.green) * factor),
        blue = (this.blue + (1f - this.blue) * factor)
    )
}
