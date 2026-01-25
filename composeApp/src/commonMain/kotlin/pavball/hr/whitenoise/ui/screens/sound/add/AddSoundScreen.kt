package pavball.hr.whitenoise.ui.screens.sound.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import org.jetbrains.compose.resources.painterResource
import pavball.hr.whitenoise.domain.model.rememberSoundPicker
import pavball.hr.whitenoise.ui.components.button.DozzzeButton
import pavball.hr.whitenoise.ui.components.dialog.AddCustomSoundDialog
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.theme.LightGray
import pavball.hr.whitenoise.ui.theme.Mint
import pavball.hr.whitenoise.ui.theme.SlateBlue
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.bg_blue
import whitenoise.composeapp.generated.resources.ic_add

@Composable
internal fun AddSoundScreen(modifier: Modifier = Modifier, viewModel: MainScreenViewModel) {

    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

    val pendingRename by viewModel.pendingRename.collectAsState()

    var sliderValue by remember { mutableStateOf(0.5f) }


    // Sound Picker Launcher
    val launchSoundPicker = rememberSoundPicker { picked ->
        if (picked != null) {
            viewModel.addUserSound(picked.displayName, picked.uri, picked.colorId)
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

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Add Sound",
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal,
                color = Mint
            )

            SpacerHelper(90.dp)

            DozzzeButton(
                modifier = modifier.padding(bottom = 16.dp),
                onClicked = { },
                buttonText = "Playlist",
                buttonWidth = 250.dp,
                buttonTextStyle = MaterialTheme.typography.bodyMedium,
                buttonTextFont = quickSandFont,
                buttonTextWeight = FontWeight.Bold,
                shape = RoundedCornerShape(20.dp)
            )

            DozzzeButton(
                modifier = modifier.padding(bottom = 16.dp),
                onClicked = { },
                buttonText = "Library",
                buttonWidth = 250.dp,
                buttonTextStyle = MaterialTheme.typography.bodyMedium,
                buttonTextFont = quickSandFont,
                buttonTextWeight = FontWeight.Bold,
                shape = RoundedCornerShape(20.dp)
            )

            DozzzeButton(
                modifier = modifier.padding(bottom = 16.dp),
                onClicked = { },
                buttonText = "Downloads",
                buttonWidth = 250.dp,
                buttonTextStyle = MaterialTheme.typography.bodyMedium,
                buttonTextFont = quickSandFont,
                buttonTextWeight = FontWeight.Bold,
                shape = RoundedCornerShape(20.dp)
            )

            DozzzeButton(
                modifier = modifier.padding(bottom = 28.dp),
                onClicked = { },
                buttonText = "My Spotify",
                buttonWidth = 250.dp,
                buttonTextStyle = MaterialTheme.typography.bodyMedium,
                buttonTextFont = quickSandFont,
                buttonTextWeight = FontWeight.Bold,
                shape = RoundedCornerShape(20.dp)
            )

            Column(
                modifier = modifier.fillMaxWidth().padding(horizontal = 82.dp),
                horizontalAlignment = Alignment.Start
            ) {

                Text(
                    text = "Volume",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = quickSandFont,
                    color = LightGray
                )

                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors()
                        .copy(activeTrackColor = LightGray, thumbColor = SlateBlue),
                )
            }

            SpacerHelper(24.dp)

            IconButton(
                onClick = { launchSoundPicker() },
                modifier = Modifier.align(Alignment.CenterHorizontally).size(64.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_add),
                    contentDescription = "Add Sound",
                    tint = LightGray
                )
            }


        }
    }

    // --- Rename Dialog Logic ---
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


