package pavball.hr.whitenoise.domain.model

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberSoundPicker(
    onPicked: (UserSound?) -> Unit
): () -> Unit {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                val name = uri.lastPathSegment ?: "Custom Sound"
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                onPicked(UserSound(name, uri.toString()))
            } else {
                onPicked(null)
            }
        }
    )

    return remember {
        { launcher.launch(arrayOf("audio/*")) }
    }
}