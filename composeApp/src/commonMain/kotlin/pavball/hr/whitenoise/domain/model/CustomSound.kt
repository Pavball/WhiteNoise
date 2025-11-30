package pavball.hr.whitenoise.domain.model

import kotlinx.serialization.Serializable
import pavball.hr.whitenoise.db.Custom_sounds
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
data class CustomSound @OptIn(ExperimentalTime::class) constructor(
    val id: String,         // unique id (we use the uri string by default, but you can change)
    val displayName: String,
    val uri: String,
    val addedAt: Long = Clock.System.now().toEpochMilliseconds(),
    val colorId: String
)

internal fun Custom_sounds.toCustomSound() =
    CustomSound(
        id = id,
        displayName = display_name,
        uri = uri,
        addedAt = added_at,
        colorId = colorId
    )