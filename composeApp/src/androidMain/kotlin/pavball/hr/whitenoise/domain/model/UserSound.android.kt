package pavball.hr.whitenoise.domain.model

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberSoundPicker(
    onPicked: (CustomSound?) -> Unit
): () -> Unit {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                // try to derive a friendly name
                val raw = uri.lastPathSegment
                val defaultName = raw?.substringAfterLast('/') ?: "Custom sound"
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                onPicked(CustomSound(id = uri.toString(), displayName = defaultName))
            } else {
                onPicked(null)
            }
        }
    )

    return remember { { launcher.launch(arrayOf("audio/*")) } }
}
