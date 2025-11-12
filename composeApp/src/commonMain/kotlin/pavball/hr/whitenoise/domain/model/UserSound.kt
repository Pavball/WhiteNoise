package pavball.hr.whitenoise.domain.model

import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

@Serializable
data class UserSound(
    val name: String,
    val uri: String
)

@Composable
expect fun rememberSoundPicker(
    onPicked: (UserSound?) -> Unit
): () -> Unit