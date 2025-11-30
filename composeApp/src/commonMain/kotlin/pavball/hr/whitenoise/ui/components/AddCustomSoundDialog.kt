package pavball.hr.whitenoise.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import pavball.hr.whitenoise.ui.screens.home.fromHex
import pavball.hr.whitenoise.ui.screens.home.lighten

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddCustomSoundDialog(
    currentName: String,
    currentColorHex: String?,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit,
    onColorChange: (String) -> Unit,
    controller: ColorPickerController
) {
    var text by remember { mutableStateOf(currentName) }

    // initial color
    val initialColor = remember(currentColorHex) {
        if (currentColorHex != null) Color.fromHex(currentColorHex)
        else Color(0xFF3F51B5) // fallback
    }

    // live updated color - LOCAL ONLY!
    var pickedColor by remember { mutableStateOf(initialColor) }
    var pickedColorHex by remember { mutableStateOf(currentColorHex ?: "3F51B5") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = true)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {

            Text("Edit Sound Name", style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // Gradient preview based on locally picked color
            val gradientColors = listOf(pickedColor, pickedColor.lighten(0.35f))


            Text("Choose Color Background", style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(gradientColors))
            )

            Spacer(Modifier.height(24.dp))

            HsvColorPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                controller = controller,
                onColorChanged = { envelope ->
                    pickedColorHex = envelope.hexCode
                    pickedColor = Color.fromHex(envelope.hexCode)
                }
            )

            Spacer(Modifier.height(16.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text("Cancel") }

                Spacer(Modifier.width(6.dp))

                TextButton(
                    onClick = {
                        val trimmed = text.trim()
                        if (trimmed.isNotEmpty()) {
                            onRename(trimmed)
                            onColorChange(pickedColorHex) // ✔ APPLY TO DB NOW
                        }
                        onDismiss()
                    }
                ) {
                    Text("Save")
                }
            }
        }
    }
}


