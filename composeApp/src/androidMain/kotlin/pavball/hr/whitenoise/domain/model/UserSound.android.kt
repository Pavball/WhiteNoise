package pavball.hr.whitenoise.domain.model

import android.content.Intent
import android.provider.OpenableColumns
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

    fun getDisplayName(uri: android.net.Uri): String {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && nameIndex != -1) {
                it.getString(nameIndex)
            } else {
                uri.lastPathSegment ?: "Custom sound"
            }
        } ?: "Custom sound"
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                // permanently keep permission
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                val name = getDisplayName(uri)

                onPicked(
                    CustomSound(
                        id = uri.toString(),
                        displayName = name,
                        uri = uri.toString(),
                        addedAt = System.currentTimeMillis()
                    )
                )
            } else {
                onPicked(null)
            }
        }
    )

    return remember { { launcher.launch(arrayOf("audio/*")) } }
}
