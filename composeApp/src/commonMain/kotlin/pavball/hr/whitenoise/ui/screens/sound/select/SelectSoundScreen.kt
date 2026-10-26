package pavball.hr.whitenoise.ui.screens.sound.select

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.navigation.NavController
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pavball.hr.whitenoise.domain.model.CustomSound
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.components.utils.resolveGradientColors
import pavball.hr.whitenoise.ui.screens.main.Screens
import pavball.hr.whitenoise.ui.theme.Mint
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.add_custom_sound
import whitenoise.composeapp.generated.resources.bg_moonlight
import whitenoise.composeapp.generated.resources.city
import whitenoise.composeapp.generated.resources.ic_add
import whitenoise.composeapp.generated.resources.nature
import whitenoise.composeapp.generated.resources.ocean
import whitenoise.composeapp.generated.resources.rain
import whitenoise.composeapp.generated.resources.sounds
import whitenoise.composeapp.generated.resources.your_sounds

/** Artwork for the built-in sounds, keyed like [MainScreenViewState.defaultSounds]. */
private val builtInArtwork: Map<String, DrawableResource> = mapOf(
    "rain" to Res.drawable.rain,
    "ocean" to Res.drawable.ocean,
    "forest" to Res.drawable.nature,
    "thunder" to Res.drawable.city
)

@Composable
internal fun SelectSoundScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MainScreenViewModel
) {

    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

    val state by viewModel.viewState<MainScreenViewState>()
        .collectAsState(initial = MainScreenViewState())

    val onSoundPicked: (String) -> Unit = { id ->
        viewModel.playSound(id)
        navController.popBackStack()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Background Layer
        Image(
            painter = painterResource(Res.drawable.bg_moonlight),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Dark Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        )

        // 3. Main Content
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- Header ---
            Text(
                text = stringResource(Res.string.sounds),
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal,
                color = Mint
            )

            SpacerHelper(20.dp)

            // --- Grid ---
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp), // More vertical space for text
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.sounds, key = { it.second }) { (label, id) ->
                    SoundCard(
                        name = label,
                        isSelected = state.selectedSoundKey == id,
                        fontFamily = quickSandFont,
                        onClick = { onSoundPicked(id) }
                    ) {
                        builtInArtwork[id]?.let { art ->
                            Image(
                                painter = painterResource(art),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } ?: GradientArt(resolveGradientColors(id, null))
                    }
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = stringResource(Res.string.your_sounds),
                        fontFamily = quickSandFont,
                        fontWeight = FontWeight.Bold,
                        color = Mint,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(state.customSounds, key = { it.id }) { sound ->
                    SoundCard(
                        name = sound.displayName,
                        isSelected = state.selectedSoundKey == sound.id,
                        fontFamily = quickSandFont,
                        onClick = { onSoundPicked(sound.id) }
                    ) {
                        CustomSoundArt(sound, quickSandFont)
                    }
                }

                item {
                    SoundCard(
                        name = stringResource(Res.string.add_custom_sound),
                        isSelected = false,
                        fontFamily = quickSandFont,
                        onClick = { navController.navigate(Screens.AddSound.route) }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_add),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SoundCard(
    name: String,
    isSelected: Boolean,
    fontFamily: FontFamily,
    onClick: () -> Unit,
    artwork: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }
    ) {
        // Image Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // Makes it a square
                .clip(RoundedCornerShape(24.dp)) // heavily rounded corners like image
                .border(
                    width = if (isSelected) 3.dp else 0.dp,
                    color = if (isSelected) Mint else Color.Transparent,
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            artwork()

            // Light overlay on the selected item to make it pop
            if (isSelected) {
                Box(modifier = Modifier.fillMaxSize().background(Mint.copy(alpha = 0.2f)))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Label
        Text(
            text = name,
            fontFamily = fontFamily,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun GradientArt(colors: List<Color>, content: @Composable () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.Center
    ) { content() }
}

/** Custom sounds have no artwork, so show their color with the first letter. */
@Composable
private fun CustomSoundArt(sound: CustomSound, fontFamily: FontFamily) {
    GradientArt(resolveGradientColors(sound.id, sound.colorId)) {
        Text(
            text = sound.displayName.trim().take(1).uppercase(),
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 56.sp
        )
    }
}
