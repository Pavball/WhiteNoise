package pavball.hr.whitenoise.ui.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jetbrains.compose.resources.stringResource
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.cancel
import whitenoise.composeapp.generated.resources.mix_name
import whitenoise.composeapp.generated.resources.save
import whitenoise.composeapp.generated.resources.save_mix

/** Asks for a name for the current mix; [onSave] gets the trimmed, non-blank name. */
@Composable
fun SaveMixDialog(
    suggestedName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    // The suggestion starts selected, so typing replaces it.
    var text by remember {
        mutableStateOf(TextFieldValue(suggestedName, TextRange(0, suggestedName.length)))
    }
    val focusRequester = remember { FocusRequester() }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {
            Text(stringResource(Res.string.save_mix), style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(stringResource(Res.string.mix_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
            )
            LaunchedEffect(Unit) { focusRequester.requestFocus() }

            Spacer(Modifier.height(16.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) }
                TextButton(
                    enabled = text.text.isNotBlank(),
                    onClick = {
                        onSave(text.text.trim())
                        onDismiss()
                    }
                ) {
                    Text(stringResource(Res.string.save))
                }
            }
        }
    }
}
