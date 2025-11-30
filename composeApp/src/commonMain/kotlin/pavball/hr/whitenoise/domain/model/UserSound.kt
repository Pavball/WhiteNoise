package pavball.hr.whitenoise.domain.model

import androidx.compose.runtime.Composable

@Composable
expect fun rememberSoundPicker(
    onPicked: (CustomSound?) -> Unit
): () -> Unit