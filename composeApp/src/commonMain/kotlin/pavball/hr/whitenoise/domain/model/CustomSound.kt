package pavball.hr.whitenoise.domain.model

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
data class CustomSound @OptIn(ExperimentalTime::class) constructor(
    val id: String,              // The URI or resource key
    val displayName: String,     // User-chosen name
    val addedAt: Long = Clock.System.now().toEpochMilliseconds()
)