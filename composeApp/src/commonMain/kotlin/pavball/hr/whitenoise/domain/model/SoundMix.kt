package pavball.hr.whitenoise.domain.model

import pavball.hr.whitenoise.db.Sound_mix

/**
 * A main sound plus extra sounds layered on top of it, each with its own
 * volume (0..1). Built-in [suggested] mixes have negative ids.
 */
data class SoundMix(
    val id: Long,
    val name: String,
    val leadSoundId: String,
    val leadVolume: Float,
    val layers: Map<String, Float>
) {
    val isSuggested: Boolean get() = id < 0

    companion object {
        /** Most sounds that may play on top of the main one at once. */
        const val MAX_LAYERS = 5

        const val SUGGESTED_STORMY_NIGHT = -1L
        const val SUGGESTED_COSY_RAIN = -2L
        const val SUGGESTED_DEEP_FOCUS = -3L
        const val SUGGESTED_SEASIDE = -4L

        /** Ready-made mixes; the UI shows them under localized names. */
        val suggested = listOf(
            SoundMix(SUGGESTED_STORMY_NIGHT, "Stormy Night", "thunder", 1f, mapOf("brown_noise" to 0.35f)),
            SoundMix(SUGGESTED_COSY_RAIN, "Cosy Rain", "rain", 0.9f, mapOf("forest" to 0.4f, "pink_noise" to 0.15f)),
            SoundMix(SUGGESTED_DEEP_FOCUS, "Deep Focus", "brown_noise", 0.8f, mapOf("pink_noise" to 0.25f)),
            SoundMix(SUGGESTED_SEASIDE, "Seaside", "ocean", 1f, mapOf("rain" to 0.2f))
        )

        /** Stores layers as "soundId=volume;…". Sound ids (content uris) may contain any character. */
        fun encodeLayers(layers: Map<String, Float>): String =
            layers.entries.joinToString(";") { (id, volume) -> "${escape(id)}=$volume" }

        fun decodeLayers(encoded: String): Map<String, Float> =
            encoded.split(';').mapNotNull { entry ->
                val parts = entry.split('=')
                if (parts.size != 2) return@mapNotNull null
                val volume = parts[1].toFloatOrNull() ?: return@mapNotNull null
                unescape(parts[0]) to volume.coerceIn(0f, 1f)
            }.toMap()

        private fun escape(id: String) =
            id.replace("%", "%25").replace(";", "%3B").replace("=", "%3D")

        private fun unescape(id: String) =
            id.replace("%3B", ";").replace("%3D", "=").replace("%25", "%")
    }
}

internal fun Sound_mix.toSoundMix() =
    SoundMix(
        id = id,
        name = name,
        leadSoundId = lead_sound_id,
        leadVolume = lead_volume.toFloat(),
        layers = SoundMix.decodeLayers(layers)
    )
