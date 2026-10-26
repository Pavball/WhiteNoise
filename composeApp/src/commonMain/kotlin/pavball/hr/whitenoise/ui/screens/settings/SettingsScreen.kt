package pavball.hr.whitenoise.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import pavball.hr.whitenoise.platform.BackgroundPlayback
import pavball.hr.whitenoise.ui.components.utils.ArrowIcon
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.theme.LightGray
import pavball.hr.whitenoise.ui.theme.Mint
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState.Companion.ThemeMode
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.background_optimized
import whitenoise.composeapp.generated.resources.background_playback
import whitenoise.composeapp.generated.resources.background_unrestricted
import whitenoise.composeapp.generated.resources.bg_green
import whitenoise.composeapp.generated.resources.fade_out_time
import whitenoise.composeapp.generated.resources.my_sounds
import whitenoise.composeapp.generated.resources.sec
import whitenoise.composeapp.generated.resources.settings
import whitenoise.composeapp.generated.resources.theme
import whitenoise.composeapp.generated.resources.theme_dark
import whitenoise.composeapp.generated.resources.theme_light
import whitenoise.composeapp.generated.resources.theme_system

private fun themeLabel(mode: String): StringResource = when (mode) {
    ThemeMode.LIGHT.name -> Res.string.theme_light
    ThemeMode.DARK.name -> Res.string.theme_dark
    else -> Res.string.theme_system
}

@Composable
internal fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel,
    onOpenMySounds: () -> Unit,
    backgroundPlayback: BackgroundPlayback = koinInject()
) {
    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

    val state by viewModel.viewState<MainScreenViewState>()
        .collectAsState(initial = MainScreenViewState())
    var themeExpanded by rememberSaveable { mutableStateOf(false) }
    var fadeOutExpanded by rememberSaveable { mutableStateOf(false) }

    // Re-checked on resume, e.g. after coming back from the system battery settings.
    var isUnrestricted by remember { mutableStateOf(backgroundPlayback.isUnrestricted()) }
    LifecycleResumeEffect(Unit) {
        isUnrestricted = backgroundPlayback.isUnrestricted()
        onPauseOrDispose { }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.bg_green),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SpacerHelper(32.dp)

            Text(
                text = stringResource(Res.string.settings),
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal,
                color = Mint
            )

            SpacerHelper(8.dp)

            // --- Theme ---
            Box {
                SettingsRow(
                    title = stringResource(Res.string.theme),
                    value = stringResource(themeLabel(state.themeMode)),
                    font = quickSandFont,
                    onClick = { themeExpanded = true }
                )
                DropdownMenu(expanded = themeExpanded, onDismissRequest = { themeExpanded = false }) {
                    state.themeOptions.forEach { mode ->
                        DropdownMenuItem(
                            text = { Text(stringResource(themeLabel(mode)), fontFamily = quickSandFont) },
                            onClick = {
                                themeExpanded = false
                                viewModel.saveThemeModeToUserPrefs(mode)
                            }
                        )
                    }
                }
            }

            // --- Fade-out ---
            Box {
                SettingsRow(
                    title = stringResource(Res.string.fade_out_time),
                    value = "${state.fadeDuration} ${stringResource(Res.string.sec)}",
                    font = quickSandFont,
                    onClick = { fadeOutExpanded = true }
                )
                DropdownMenu(expanded = fadeOutExpanded, onDismissRequest = { fadeOutExpanded = false }) {
                    state.fadeOptions.forEach { seconds ->
                        DropdownMenuItem(
                            text = { Text("$seconds ${stringResource(Res.string.sec)}", fontFamily = quickSandFont) },
                            onClick = {
                                fadeOutExpanded = false
                                viewModel.saveFadeOutTimeToUserPrefs(seconds)
                            }
                        )
                    }
                }
            }

            // --- My sounds ---
            SettingsRow(
                title = stringResource(Res.string.my_sounds),
                value = state.customSounds.size.toString(),
                font = quickSandFont,
                onClick = onOpenMySounds
            )

            // --- Background playback ---
            SettingsRow(
                title = stringResource(Res.string.background_playback),
                value = null,
                description = stringResource(
                    if (isUnrestricted) Res.string.background_unrestricted else Res.string.background_optimized
                ),
                font = quickSandFont,
                onClick = { backgroundPlayback.openSettings() },
                highlight = !isUnrestricted
            )

            SpacerHelper(32.dp)
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    value: String?,
    font: FontFamily,
    onClick: () -> Unit,
    description: String? = null,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .border(BorderStroke(1.dp, if (highlight) Color(0xFFFFB74D) else Mint), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontFamily = font,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
            description?.let {
                Text(
                    text = it,
                    color = LightGray.copy(alpha = 0.75f),
                    fontFamily = font,
                    fontSize = 14.sp
                )
            }
        }
        value?.let {
            Text(
                text = it,
                color = LightGray,
                fontFamily = font,
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        ArrowIcon()
    }
}
